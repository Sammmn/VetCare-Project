package com.backend.vetcare.dto.response;

public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEmSegundos,
        UsuarioResponseDTO usuario
) {
}
