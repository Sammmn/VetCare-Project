package com.backend.vetcare.service;

import com.backend.vetcare.dto.request.ConsultaRequestDTO;
import com.backend.vetcare.dto.request.RegistroAtendimentoDTO;
import com.backend.vetcare.dto.response.ConsultaResponseDTO;
import com.backend.vetcare.model.*;
import com.backend.vetcare.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;

    public ConsultaService(ConsultaRepository consultaRepository, PetRepository petRepository, VeterinarioRepository veterinarioRepository) {
        this.consultaRepository = consultaRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    public List<ConsultaResponseDTO> listarTodas() {
        return consultaRepository.findAll().stream().map(ConsultaResponseDTO::new).collect(Collectors.toList());
    }

    public List<ConsultaResponseDTO> obterAgendaProfissional(Long profissionalId, LocalDate data) {
        LocalDate dataConsulta = (data != null) ? data : LocalDate.now();
        return consultaRepository.buscarAgendaPorProfissionalEPeriodo(profissionalId, dataConsulta.atStartOfDay(), dataConsulta.atTime(LocalTime.MAX))
                .stream().map(ConsultaResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public ConsultaResponseDTO agendar(ConsultaRequestDTO dto) {
        if (dto.getPetId() == null || dto.getVeterinarioId() == null || dto.getInicio() == null) {
            throw new IllegalArgumentException("Pet, veterinário e horário de início são obrigatórios.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com o ID: " + dto.getPetId()));
        if (pet.getTutor() == null) {
            throw new IllegalArgumentException("O pet selecionado não possui tutor vinculado.");
        }
        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com o ID: " + dto.getVeterinarioId()));

        LocalDateTime inicio = dto.getInicio();
        List<Consulta> existentes = consultaRepository.buscarAgendaPorProfissionalEPeriodo(vet.getId(), inicio.minusMinutes(59), inicio.plusMinutes(59));
        if (existentes.stream().anyMatch(c -> c.getStatus() != StatusConsulta.CANCELADA)) {
            throw new IllegalArgumentException("Conflito de horário: este veterinário já possui agendamento nesse horário.");
        }

        Consulta consulta = new Consulta();
        consulta.setPet(pet);
        consulta.setTutor(pet.getTutor());
        consulta.setVeterinario(vet);
        consulta.setInicio(inicio);
        consulta.setFim(inicio.plusHours(1));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(dto.getObservacoes());
        return new ConsultaResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponseDTO cancelar(Long consultaId) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + consultaId));
        consulta.setStatus(StatusConsulta.CANCELADA);
        return new ConsultaResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaResponseDTO marcarComoRealizada(Long consultaId, Long profissionalId, RegistroAtendimentoDTO dados) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com o ID: " + consultaId));
        if (consulta.getVeterinario() == null || !consulta.getVeterinario().getId().equals(profissionalId)) {
            throw new IllegalArgumentException("Acesso negado: o profissional informado não é o responsável por esta consulta.");
        }
        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new IllegalArgumentException("Não é possível realizar uma consulta que foi cancelada.");
        }
        consulta.setStatus(StatusConsulta.REALIZADA);
        consulta.setDiagnostico(dados.getDiagnostico());
        consulta.setPrescricao(dados.getPrescricao());
        consulta.setObservacoes(dados.getObservacoes());
        return new ConsultaResponseDTO(consultaRepository.save(consulta));
    }
}