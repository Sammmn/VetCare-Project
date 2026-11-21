package com.backend.vetcare.exception;

import org.springframework.http.HttpStatus;

public class ConflitoException extends ApiException {

    public ConflitoException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
