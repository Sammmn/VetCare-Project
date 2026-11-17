package com.backend.vetcare.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PetDTO {

    private String nome;
    private String especie;
    private String raca;
    private Integer idade;
    private Double peso;
    private String sexo;
    private String porte;
    private String cor;
    private LocalDate dataNascimento;
    private String observacoes;
    private Long tutorId;
}
