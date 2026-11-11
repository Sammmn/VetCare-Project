package com.backend.vetcare.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VeterinarioDTO extends UsuarioDTO{

    private String registroProfissional;
    private String biografia;
    private BigDecimal valorConsulta;
}