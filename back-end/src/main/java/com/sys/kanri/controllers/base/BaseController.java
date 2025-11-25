package com.sys.kanri.controllers.base;

import com.sys.kanri.dto.ApiSuccessResponse;
import com.sys.kanri.utils.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<ApiSuccessResponse<T>> ok(T data, String message) {
        return ResponseFactory.ok(data, message);
    }

    protected <T> ResponseEntity<ApiSuccessResponse<T>> ok(T data) {
        return ResponseFactory.ok(data, "");
    }

    protected <T> ResponseEntity<ApiSuccessResponse<T>> created(T data, String message) {
        return ResponseFactory.success(data, message, HttpStatus.CREATED);
    }

    protected <T> ResponseEntity<ApiSuccessResponse<T>> customStatus(T data, String message, HttpStatus status) {
        return ResponseFactory.success(data, message, status);
    }
}