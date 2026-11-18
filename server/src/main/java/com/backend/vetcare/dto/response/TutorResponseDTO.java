package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Tutor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorResponseDTO extends UsuarioResponseDTO {

    private String cpf;

    public TutorResponseDTO(Tutor tutor) {
        super(tutor);
        this.cpf = tutor.getCpf();
    }
}
