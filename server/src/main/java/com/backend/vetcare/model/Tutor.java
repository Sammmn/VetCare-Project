package com.backend.vetcare.model;

import jakarta.persistence.Entity;

@Entity
public class Tutor extends Usuario{

    public Tutor(){

        setCargo(Cargo.TUTOR);
    }

}
