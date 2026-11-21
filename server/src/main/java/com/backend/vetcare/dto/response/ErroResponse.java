package com.backend.vetcare.dto.response;

import java.util.Map;

public record ErroResponse(
        String timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos
) {
}
