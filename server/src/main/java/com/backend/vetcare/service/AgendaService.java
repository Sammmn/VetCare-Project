package com.backend.vetcare.service;

import com.backend.vetcare.dto.response.HorarioDisponivelDTO;
import com.backend.vetcare.dto.response.HorariosDisponiveisPorDataDTO;
import com.backend.vetcare.model.Agenda;
import com.backend.vetcare.repositories.AgendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public AgendaService(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    public HorariosDisponiveisPorDataDTO buscarHorariosDisponiveis(Long veterinarioId, LocalDate data) {
        List<Agenda> agendas = agendaRepository.findByVeterinarioIdAndData(veterinarioId, data);

        return new HorariosDisponiveisPorDataDTO(
                data,
                agendas.stream()
                        .map(agenda -> new HorarioDisponivelDTO(agenda.getHorario(), agenda.isDisponivel()))
                        .collect(Collectors.toList())
        );
    }

    public Agenda criarAgenda(Agenda agenda) {
        return agendaRepository.save(agenda);
    }
}
