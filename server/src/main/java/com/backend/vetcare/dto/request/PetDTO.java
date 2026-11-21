package com.backend.vetcare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PetDTO {

    @NotBlank(message = "O nome do pet é obrigatório.")
    private String nome;

    @NotBlank(message = "A espécie é obrigatória.")
    private String especie;

    private String raca;

    @PositiveOrZero(message = "A idade não pode ser negativa.")
    private Integer idade;

    @Positive(message = "O peso deve ser maior que zero.")
    private Double peso;

    private String sexo;

    private String porte;

    private String cor;

    @PastOrPresent(message = "A data de nascimento não pode ser futura.")
    private LocalDate dataNascimento;

    private String observacoes;

    /** Obrigatório apenas para ADMIN; o TUTOR logado é sempre o dono. */
    private Long tutorId;
}
