package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Veterinario;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VeterinarioResponseDTO extends UsuarioResponseDTO {

    private String registroProfissional;
    private String biografia;
    private BigDecimal valorConsulta;

    public VeterinarioResponseDTO(Veterinario veterinario) {
        super(veterinario);
        this.registroProfissional = veterinario.getRegistroProfissional();
        this.biografia = veterinario.getBiografia();
        this.valorConsulta = veterinario.getValorConsulta();
    }
}
