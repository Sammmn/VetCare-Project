package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Usuario;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class VeterinarioResponseDTO extends UsuarioResponseDTO {

    private String registroProfissional;
    private String biografia;
    private BigDecimal valorConsulta;

    public VeterinarioResponseDTO(Usuario usuario) {
        super(usuario);
    }
}
