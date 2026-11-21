package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VeterinarioDTO extends UsuarioDTO {

    @NotBlank(message = "O registro profissional (CRMV) é obrigatório.")
    private String registroProfissional;

    private String biografia;

    @NotNull(message = "O valor da consulta é obrigatório.")
    @PositiveOrZero(message = "O valor da consulta não pode ser negativo.")
    private BigDecimal valorConsulta;
}
