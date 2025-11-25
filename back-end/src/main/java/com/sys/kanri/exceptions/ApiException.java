package com.sys.kanri.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}