package com.backend.vetcare.exception;

import org.springframework.http.HttpStatus;

public class RegraNegocioException extends ApiException {

    public RegraNegocioException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
