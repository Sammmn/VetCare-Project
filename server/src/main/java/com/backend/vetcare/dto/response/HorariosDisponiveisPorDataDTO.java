package com.backend.vetcare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorariosDisponiveisPorDataDTO {

    private Long veterinarioId;
    private LocalDate data;
    private List<HorarioDisponivelDTO> horarios;
}
