package com.backend.vetcare.model;

import lombok.Getter;

@Getter
public enum Cargo {

    ADMIN("ADMIN"),
    RECEPCIONISTA("RECEPCIONISTA"),
    VETERINARIO("VETERINARIO"),
    TUTOR("TUTOR");

    private final String cargo;

    Cargo(String cargo) {
        this.cargo = cargo;
    }
}
