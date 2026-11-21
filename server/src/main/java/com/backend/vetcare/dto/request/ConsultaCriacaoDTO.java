package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Agenda uma consulta reservando um horário. tutorId só é exigido para ADMIN. */
public record ConsultaCriacaoDTO(
        @NotNull(message = "O horário (agendaId) é obrigatório.")
        Long agendaId,

        @NotNull(message = "O pet é obrigatório.")
        Long petId,

        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres.")
        String motivo,

        Long tutorId
) {
}
