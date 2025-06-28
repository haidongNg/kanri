package com.sys.kanri.exceptions;

import com.sys.kanri.dto.ApiErrorResponse;
import com.sys.kanri.utils.ResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        return ResponseFactory.error(ex.getCode(), ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOtherExceptions(Exception ex) {
        return ResponseFactory.error("E50000", ex.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}