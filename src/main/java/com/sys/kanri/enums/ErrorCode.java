package com.sys.kanri.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // User-related
    USERNAME_EXISTS("U40901", "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_EXISTS("U40902", "Email đã được sử dụng", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("U40101", "Sai tên đăng nhập hoặc mật khẩu", HttpStatus.UNAUTHORIZED),

    // Token-related
    TOKEN_INVALID("T40101", "Token không hợp lệ", HttpStatus.UNAUTHORIZED),

    // Server or unknown errors
    INTERNAL_ERROR("E50000", "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String code;
    public final String message;
    public final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
