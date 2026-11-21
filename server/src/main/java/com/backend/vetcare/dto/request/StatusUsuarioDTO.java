package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.NotNull;

public record StatusUsuarioDTO(
        @NotNull(message = "Informe se o usuário está ativo.")
        Boolean ativo
) {
}
