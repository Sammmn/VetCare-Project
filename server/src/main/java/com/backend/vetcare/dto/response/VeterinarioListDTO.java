package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Veterinario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioListDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String registroProfissional;
    private BigDecimal valorConsulta;
    private String biografia;
    private Double avaliacao;
    private String localizacao;
    private List<String> especialidades;

    public VeterinarioListDTO(Veterinario veterinario) {
        this.id = veterinario.getId();
        this.nome = veterinario.getNome();
        this.email = veterinario.getEmail();
        this.telefone = veterinario.getTelefone();
        this.registroProfissional = veterinario.getRegistroProfissional();
        this.valorConsulta = veterinario.getValorConsulta();
        this.biografia = veterinario.getBiografia();
    }
}
