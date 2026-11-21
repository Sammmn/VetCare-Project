package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.ConsultaCriacaoDTO;
import com.backend.vetcare.dto.request.RegistroAtendimentoDTO;
import com.backend.vetcare.dto.response.ConsultaResponseDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Agenda;
import com.backend.vetcare.model.Consulta;
import com.backend.vetcare.model.Pet;
import com.backend.vetcare.model.StatusConsulta;
import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.repositories.AgendaRepository;
import com.backend.vetcare.repositories.ConsultaRepository;
import com.backend.vetcare.repositories.PetRepository;
import com.backend.vetcare.repositories.TutorRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AgendaRepository agendaRepository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final Clock clock;
    private final long duracaoMinutos;
    private final long antecedenciaCancelamentoHoras;

    public ConsultaService(ConsultaRepository consultaRepository,
                           AgendaRepository agendaRepository,
                           PetRepository petRepository,
                           TutorRepository tutorRepository,
                           Clock clock,
                           @Value("${app.consulta.duracao-minutos:30}") long duracaoMinutos,
                           @Value("${app.consulta.antecedencia-cancelamento-horas:2}") long antecedenciaCancelamentoHoras) {
        this.consultaRepository = consultaRepository;
        this.agendaRepository = agendaRepository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.clock = clock;
        this.duracaoMinutos = duracaoMinutos;
        this.antecedenciaCancelamentoHoras = antecedenciaCancelamentoHoras;
    }

    // ------------------------------------------------------------------ agendar

    @Transactional
    public ConsultaResponseDTO agendar(ConsultaCriacaoDTO dto, UsuarioAutenticado logado) {
        Long tutorId = logado.resolverTutorId(dto.tutorId());

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado."));
        if (!tutor.isAtivo()) {
            throw new RegraNegocioException("O tutor está inativo.");
        }

        Pet pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado."));
        if (!pet.getTutor().getId().equals(tutorId)) {
            throw new RegraNegocioException("O pet informado não pertence ao tutor.");
        }

        // lock pessimista: impede dois agendamentos simultâneos no mesmo horário
        Agenda agenda = agendaRepository.buscarParaReserva(dto.agendaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horário não encontrado."));
        if (!agenda.isDisponivel()) {
            throw new ConflitoException("Este horário não está mais disponível.");
        }

        Veterinario vet = agenda.getVeterinario();
        if (!vet.isAtivo()) {
            throw new RegraNegocioException("O veterinário deste horário está inativo.");
        }

        LocalDateTime inicio = LocalDateTime.of(agenda.getData(), agenda.getHorario());
        if (!inicio.isAfter(agora())) {
            throw new RegraNegocioException("Não é possível agendar em um horário que já passou.");
        }
        LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);

        if (consultaRepository.existeConflitoParaVeterinario(vet.getId(), StatusConsulta.AGENDADA, inicio, fim)) {
            throw new ConflitoException("O veterinário já possui uma consulta agendada nesse período.");
        }
        if (consultaRepository.existeConflitoParaPet(pet.getId(), StatusConsulta.AGENDADA, inicio, fim)) {
            throw new ConflitoException("O pet já possui uma consulta agendada nesse período.");
        }

        Consulta consulta = new Consulta();
        consulta.setTutor(tutor);
        consulta.setPet(pet);
        consulta.setVeterinario(vet);
        consulta.setAgenda(agenda);
        consulta.setInicio(inicio);
        consulta.setFim(fim);
        consulta.setMotivo(dto.motivo());
        consulta.setStatus(StatusConsulta.AGENDADA);

        agenda.setDisponivel(false);
        agendaRepository.save(agenda);

        return resposta(consultaRepository.save(consulta), logado);
    }

    // ------------------------------------------------------------------ consultas

    /** ADMIN vê todas; TUTOR e VETERINARIO veem apenas as suas. Filtro opcional por status. */
    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listar(UsuarioAutenticado logado, StatusConsulta status) {
        List<Consulta> consultas = switch (logado.cargo()) {
            case ADMIN, RECEPCIONISTA -> consultaRepository.findAllByOrderByInicioDesc();
            case TUTOR -> consultaRepository.findByTutorIdOrderByInicioDesc(logado.id());
            case VETERINARIO -> consultaRepository.findByVeterinarioIdOrderByInicioDesc(logado.id());
        };

        return consultas.stream()
                .filter(c -> status == null || c.getStatus() == status)
                .map(c -> resposta(c, logado))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id, UsuarioAutenticado logado) {
        Consulta consulta = buscarEntidade(id);
        garantirParticipante(consulta, logado);
        return resposta(consulta, logado);
    }

    /** RF10 - agenda do dia do veterinário (VETERINARIO: a própria; ADMIN: informa o profissionalId). */
    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> obterAgenda(UsuarioAutenticado logado, Long profissionalId, LocalDate data) {
        Long veterinarioId = logado.resolverVeterinarioId(profissionalId);
        LocalDate dia = (data != null) ? data : LocalDate.now(clock);

        return consultaRepository
                .buscarAgendaPorVeterinarioEPeriodo(veterinarioId, dia.atStartOfDay(), dia.atTime(LocalTime.MAX))
                .stream()
                .map(c -> resposta(c, logado))
                .toList();
    }

    // ------------------------------------------------------------------ realizar

    /** RF11 - registra o atendimento. Somente o veterinário responsável. */
    @Transactional
    public ConsultaResponseDTO realizar(Long id, UsuarioAutenticado logado, RegistroAtendimentoDTO dados) {
        Consulta consulta = buscarEntidade(id);

        if (!logado.isVeterinario() || !logado.isUsuario(consulta.getVeterinario().getId())) {
            throw new AcessoNegadoException("Apenas o veterinário responsável pode registrar este atendimento.");
        }
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RegraNegocioException("Não é possível realizar uma consulta que foi cancelada.");
        }
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new RegraNegocioException("Esta consulta já foi finalizada anteriormente.");
        }
        if (agora().isBefore(consulta.getInicio())) {
            throw new RegraNegocioException("A consulta só pode ser finalizada a partir do seu horário de início.");
        }

        consulta.setStatus(StatusConsulta.REALIZADA);
        consulta.setDiagnostico(dados.getDiagnostico());
        consulta.setPrescricao(dados.getPrescricao());
        consulta.setObservacoes(dados.getObservacoes());
        return resposta(consultaRepository.save(consulta), logado);
    }

    // ------------------------------------------------------------------ cancelar

    @Transactional
    public ConsultaResponseDTO cancelar(Long id, UsuarioAutenticado logado, String motivo) {
        Consulta consulta = buscarEntidade(id);
        garantirParticipante(consulta, logado);

        if (consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new RegraNegocioException("Apenas consultas agendadas podem ser canceladas.");
        }

        LocalDateTime agora = agora();
        if (!agora.isBefore(consulta.getInicio())) {
            throw new RegraNegocioException("Não é possível cancelar uma consulta que já começou.");
        }
        if (logado.isTutor() && agora.plusHours(antecedenciaCancelamentoHoras).isAfter(consulta.getInicio())) {
            throw new RegraNegocioException("O cancelamento pelo tutor exige antecedência mínima de "
                    + antecedenciaCancelamentoHoras + " hora(s). Entre em contato com a clínica.");
        }

        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setMotivoCancelamento(motivo);

        // Cancelou o tutor/admin: horário volta a ficar disponível.
        // Cancelou o veterinário: ele não poderá atender, então o horário permanece bloqueado.
        if (consulta.getAgenda() != null && !logado.isVeterinario()) {
            consulta.getAgenda().setDisponivel(true);
            agendaRepository.save(consulta.getAgenda());
        }
        return resposta(consultaRepository.save(consulta), logado);
    }

    // ------------------------------------------------------------------ helpers

    private Consulta buscarEntidade(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
    }

    private void garantirParticipante(Consulta consulta, UsuarioAutenticado logado) {
        boolean permitido = logado.isEquipeAdministrativa()
                || (logado.isTutor() && logado.isUsuario(consulta.getTutor().getId()))
                || (logado.isVeterinario() && logado.isUsuario(consulta.getVeterinario().getId()));
        if (!permitido) {
            throw new AcessoNegadoException("Você não tem acesso a esta consulta.");
        }
    }

    /** A recepção vê os dados administrativos da consulta, mas não os dados clínicos (diagnóstico, prescrição, observações). */
    private ConsultaResponseDTO resposta(Consulta consulta, UsuarioAutenticado logado) {
        ConsultaResponseDTO dto = new ConsultaResponseDTO(consulta);
        return logado.isRecepcionista() ? dto.semDadosClinicos() : dto;
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(clock);
    }
}
