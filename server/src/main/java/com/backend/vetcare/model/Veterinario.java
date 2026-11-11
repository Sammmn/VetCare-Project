package com.backend.vetcare.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Veterinario extends Usuario{

    private String registroProfissional;
    private String biografia;
    private BigDecimal valorConsulta;

}
