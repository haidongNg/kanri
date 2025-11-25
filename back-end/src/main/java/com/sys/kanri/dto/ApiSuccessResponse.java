package com.sys.kanri.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiSuccessResponse<T> {
    private Instant timestamp;
    private int status;
    private String message;
    private T data;
    private String path;
}