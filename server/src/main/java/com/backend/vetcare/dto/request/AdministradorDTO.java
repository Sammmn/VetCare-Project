package com.backend.vetcare.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministradorDTO extends UsuarioDTO {

    private String nivelAcesso;
}
