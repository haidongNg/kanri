package com.sys.kanri.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // User-related
    USERNAME_EXISTS("U40401", "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_EXISTS("U40902", "Email đã được sử dụng", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("U40101", "Sai tên đăng nhập hoặc mật khẩu", HttpStatus.UNAUTHORIZED),

    // Password-related
    OLD_PASSWORD_MISMATCH("P40001", "Mật khẩu cũ không đúng", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD("P40002", "Mật khẩu mới không được giống mật khẩu cũ", HttpStatus.BAD_REQUEST),
    PASSWORD_CHANGE_FAILED("P50001", "Không thể đổi mật khẩu", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authorization/Authentication
    UNAUTHORIZED_ACCESS("A40101", "Bạn không có quyền truy cập", HttpStatus.UNAUTHORIZED),

    // Role
    ROLE_NOT_FOUND("R40401", "Không tìm thấy quyền", HttpStatus.NOT_FOUND),
    ROLE_DUPLICATED("R40901", "Quyền đã tồn tại", HttpStatus.CONFLICT),
    ROLE_ACCESS_DENIED("R40301", "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),

    // Order errors
    ORDER_NOT_FOUND("O40401", "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_INVALID_STATUS("O40001", "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),

    // Product errors
    PRODUCT_NOT_FOUND("O40401", "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    PRODUCT_INVALID_STATUS("O40001", "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),

    // System errors
    INTERNAL_ERROR("S50001", "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String code;
    public final String message;
    public final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
