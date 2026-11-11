package com.backend.vetcare.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {

    private String email;
    private String senha;
}
