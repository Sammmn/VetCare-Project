package com.backend.vetcare.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Veterinario extends Usuario {

    @Column(unique = true)
    private String registroProfissional;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    private BigDecimal valorConsulta;

    public Veterinario() {
        setCargo(Cargo.VETERINARIO);
    }
}
