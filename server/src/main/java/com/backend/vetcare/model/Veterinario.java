package com.backend.vetcare.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Veterinario extends Usuario {

    private String registroProfissional;
    private String especialidade;
    private String biografia;
    private BigDecimal valorConsulta;

    public Veterinario() {
        setCargo(Cargo.VETERINARIO);
    }
}
