package com.sys.kanri.utils;

import com.sys.kanri.dto.ApiErrorResponse;
import com.sys.kanri.dto.ApiSuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public class ResponseFactory {

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    public static <T> ResponseEntity<ApiSuccessResponse<T>> success(T data, String message, HttpStatus status) {
        HttpServletRequest request = currentRequest();
        String path = (request != null) ? request.getRequestURI() : "N/A";

        ApiSuccessResponse<T> response = ApiSuccessResponse.<T>builder()
                .timestamp(Instant.now())
                .status(status.value())
                .message(message)
                .data(data)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiSuccessResponse<T>> ok(T data, String message) {
        return success(data, message, HttpStatus.OK);
    }

    public static ResponseEntity<ApiErrorResponse> error(String code, String message, HttpStatus status) {
        HttpServletRequest request = currentRequest();
        String path = (request != null) ? request.getRequestURI() : "N/A";

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(code)
                .path(path)
                .errors(Collections.emptyMap())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiErrorResponse> error(String code, String message, HttpStatus status, Map<String, String> errors) {
        HttpServletRequest request = currentRequest();
        String path = (request != null) ? request.getRequestURI() : "N/A";

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .code(code)
                .path(path)
                .errors(errors)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}