package com.backend.vetcare.model;

import jakarta.persistence.Entity;

@Entity
public class Recepcionista extends Usuario {

    public Recepcionista() {
        setCargo(Cargo.RECEPCIONISTA);
    }
}
