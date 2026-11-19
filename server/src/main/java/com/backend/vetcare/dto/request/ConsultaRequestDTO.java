package com.backend.vetcare.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaRequestDTO {
    private Long petId;
    private Long veterinarioId;
    private LocalDateTime inicio;
    private String observacoes;
}