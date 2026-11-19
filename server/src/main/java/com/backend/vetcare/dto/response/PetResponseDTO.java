package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDTO {
    private Long id;
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
    private String tutorNome;
    private String tutorTelefone;

    public PetResponseDTO(Pet pet) {
        this.id = pet.getId();
        this.nome = pet.getNome();
        this.especie = pet.getEspecie();
        this.raca = pet.getRaca();
        this.idade = pet.getIdade();
        this.peso = pet.getPeso();
        this.sexo = pet.getSexo();
        this.porte = pet.getPorte();
        this.cor = pet.getCor();
        this.dataNascimento = pet.getDataNascimento();
        this.observacoes = pet.getObservacoes();
        this.tutorId = (pet.getTutor() != null) ? pet.getTutor().getId() : null;
        this.tutorNome = (pet.getTutor() != null) ? pet.getTutor().getNome() : null;
        this.tutorTelefone = (pet.getTutor() != null) ? pet.getTutor().getTelefone() : null;
    }
}