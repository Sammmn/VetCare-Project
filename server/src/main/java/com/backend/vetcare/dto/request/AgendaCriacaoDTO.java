package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Abre vários horários de um veterinário em uma data. veterinarioId só é exigido para ADMIN. */
public record AgendaCriacaoDTO(
        Long veterinarioId,

        @NotNull(message = "A data é obrigatória.")
        LocalDate data,

        @NotEmpty(message = "Informe ao menos um horário.")
        List<@NotNull(message = "Horário inválido.") LocalTime> horarios
) {
}
