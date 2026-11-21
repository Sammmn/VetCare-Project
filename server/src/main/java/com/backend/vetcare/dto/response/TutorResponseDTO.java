package com.backend.vetcare.dto.response;

import com.backend.vetcare.model.Tutor;
import com.backend.vetcare.util.CpfUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TutorResponseDTO extends UsuarioResponseDTO {

    /** Dado pessoal (LGPD): as rotas que devolvem este DTO são restritas a ADMIN/VETERINARIO/o próprio tutor. */
    private String cpf;

    public TutorResponseDTO(Tutor tutor) {
        super(tutor);
        this.cpf = CpfUtil.formatar(tutor.getCpf());
    }
}
