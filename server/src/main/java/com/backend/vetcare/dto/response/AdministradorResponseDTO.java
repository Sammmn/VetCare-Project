package com.backend.vetcare.dto.response;


import com.backend.vetcare.model.Administrador;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdministradorResponseDTO extends UsuarioResponseDTO{

   private String nivelAcesso;

   public AdministradorResponseDTO(Administrador administrador) {
      super(administrador);
      this.nivelAcesso = administrador.getNivelAcesso();
   }
}
