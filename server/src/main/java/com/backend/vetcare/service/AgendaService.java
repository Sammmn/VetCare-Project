package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.AgendaCriacaoDTO;
import com.backend.vetcare.dto.response.HorarioDisponivelDTO;
import com.backend.vetcare.dto.response.HorariosDisponiveisPorDataDTO;
import com.backend.vetcare.exception.AcessoNegadoException;
import com.backend.vetcare.exception.ConflitoException;
import com.backend.vetcare.exception.RecursoNaoEncontradoException;
import com.backend.vetcare.exception.RegraNegocioException;
import com.backend.vetcare.model.Agenda;
import com.backend.vetcare.model.Veterinario;
import com.backend.vetcare.repositories.AgendaRepository;
import com.backend.vetcare.repositories.ConsultaRepository;
import com.backend.vetcare.repositories.VeterinarioRepository;
import com.backend.vetcare.security.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ConsultaRepository consultaRepository;
    private final Clock clock;
    private final long duracaoMinutos;

    public AgendaService(AgendaRepository agendaRepository,
                         VeterinarioRepository veterinarioRepository,
                         ConsultaRepository consultaRepository,
                         Clock clock,
                         @Value("${app.consulta.duracao-minutos:30}") long duracaoMinutos) {
        this.agendaRepository = agendaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.consultaRepository = consultaRepository;
        this.clock = clock;
        this.duracaoMinutos = duracaoMinutos;
    }

    @Transactional
    public HorariosDisponiveisPorDataDTO criar(AgendaCriacaoDTO dto, UsuarioAutenticado logado) {
        Long veterinarioId = logado.resolverVeterinarioId(dto.veterinarioId());
        Veterinario vet = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado."));
        if (!vet.isAtivo()) {
            throw new RegraNegocioException("O veterinário está inativo.");
        }

        LocalDate data = dto.data();
        LocalDate hoje = LocalDate.now(clock);
        if (data.isBefore(hoje)) {
            throw new RegraNegocioException("Não é possível criar agenda em uma data passada.");
        }

        SortedSet<LocalTime> novos = new TreeSet<>(dto.horarios());
        if (data.isEqual(hoje)) {
            LocalTime agora = LocalTime.now(clock);
            if (novos.stream().anyMatch(h -> !h.isAfter(agora))) {
                throw new RegraNegocioException("Não é possível criar horários que já passaram.");
            }
        }

        List<Agenda> existentes = agendaRepository.findByVeterinarioIdAndDataOrderByHorarioAsc(veterinarioId, data);

        // 1) horário já cadastrado
        SortedSet<LocalTime> jaCadastrados = new TreeSet<>();
        for (Agenda a : existentes) {
            if (novos.contains(a.getHorario())) {
                jaCadastrados.add(a.getHorario());
            }
        }
        if (!jaCadastrados.isEmpty()) {
            throw new ConflitoException("Horários já cadastrados para esta data: " + formatar(jaCadastrados));
        }

        // 2) intervalo mínimo entre horários (evita o veterinário ficar com consultas sobrepostas)
        SortedSet<LocalTime> todos = new TreeSet<>(novos);
        existentes.forEach(a -> todos.add(a.getHorario()));
        LocalTime anterior = null;
        for (LocalTime atual : todos) {
            if (anterior != null && Duration.between(anterior, atual).toMinutes() < duracaoMinutos) {
                throw new RegraNegocioException(
                        "Os horários devem ter intervalo mínimo de " + duracaoMinutos + " minutos entre si.");
            }
            anterior = atual;
        }

        List<Agenda> criadas = new ArrayList<>();
        for (LocalTime horario : novos) {
            Agenda agenda = new Agenda();
            agenda.setVeterinario(vet);
            agenda.setData(data);
            agenda.setHorario(horario);
            agenda.setDisponivel(true);
            criadas.add(agenda);
        }
        return montar(veterinarioId, data, agendaRepository.saveAll(criadas));
    }

    @Transactional(readOnly = true)
    public HorariosDisponiveisPorDataDTO buscarHorarios(Long veterinarioId, LocalDate data, boolean apenasDisponiveis) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new RecursoNaoEncontradoException("Veterinário não encontrado.");
        }

        List<Agenda> agendas = agendaRepository.findByVeterinarioIdAndDataOrderByHorarioAsc(veterinarioId, data);

        if (apenasDisponiveis) {
            LocalDate hoje = LocalDate.now(clock);
            LocalTime agora = LocalTime.now(clock);
            agendas = agendas.stream()
                    .filter(Agenda::isDisponivel)
                    .filter(a -> data.isAfter(hoje) || (data.isEqual(hoje) && a.getHorario().isAfter(agora)))
                    .toList();
        }
        return montar(veterinarioId, data, agendas);
    }

    @Transactional
    public void remover(Long agendaId, UsuarioAutenticado logado) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Horário não encontrado."));

        if (!logado.isEquipeAdministrativa() && !logado.isUsuario(agenda.getVeterinario().getId())) {
            throw new AcessoNegadoException("Este horário pertence a outro veterinário.");
        }
        if (consultaRepository.existsByAgendaId(agendaId)) {
            throw new ConflitoException(
                    "O horário possui consultas vinculadas e não pode ser removido.");
        }
        agendaRepository.delete(agenda);
    }

    private HorariosDisponiveisPorDataDTO montar(Long veterinarioId, LocalDate data, List<Agenda> agendas) {
        return new HorariosDisponiveisPorDataDTO(
                veterinarioId, data, agendas.stream().map(HorarioDisponivelDTO::new).toList());
    }

    private String formatar(SortedSet<LocalTime> horarios) {
        return horarios.stream().map(LocalTime::toString).collect(Collectors.joining(", "));
    }
}
