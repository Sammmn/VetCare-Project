package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.Size;

public record CancelamentoDTO(
        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres.")
        String motivo
) {
}
