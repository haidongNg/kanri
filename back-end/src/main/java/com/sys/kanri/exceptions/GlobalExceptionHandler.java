package com.sys.kanri.exceptions;

import com.sys.kanri.dto.ApiErrorResponse;
import com.sys.kanri.utils.ResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Thêm Logger để ghi lại lỗi
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Lớp chứa các hằng số mã lỗi
    private static final class ErrorCodes {
        public static final String UNEXPECTED_ERROR = "E50000";
        public static final String VALIDATION_FAILED = "E40001";
        public static final String MALFORMED_JSON = "E40002";
        public static final String METHOD_NOT_SUPPORTED = "E40500";
    }

    /**
     * Xử lý các exception nghiệp vụ có kiểm soát (ApiException).
     * Đây là những lỗi dự kiến trong luồng ứng dụng.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        // Ghi log ở mức WARN vì đây là lỗi nghiệp vụ, không phải lỗi hệ thống
        logger.warn("Business logic exception: Code[{}], Message[{}]", ex.getCode(), ex.getMessage());
        return ResponseFactory.error(ex.getCode(), ex.getMessage(), ex.getStatus());
    }

    /**
     * Xử lý lỗi validation cho RequestBody DTOs (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Sử dụng Stream API để thu thập lỗi, trông gọn gàng hơn
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value")
                ));

        logger.warn("Validation failed: {}", errors);
        return ResponseFactory.error(ErrorCodes.VALIDATION_FAILED, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * Xử lý lỗi khi client gửi sai phương thức HTTP (ví dụ: POST tới endpoint yêu cầu GET).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("Phương thức '%s' không được hỗ trợ. Các phương thức được hỗ trợ là %s.",
                ex.getMethod(),
                ex.getSupportedHttpMethods());

        logger.warn(message);
        return ResponseFactory.error(ErrorCodes.METHOD_NOT_SUPPORTED, message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Xử lý lỗi khi JSON trong request body bị sai định dạng.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedJsonException(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseFactory.error(ErrorCodes.MALFORMED_JSON, "Định dạng JSON không hợp lệ", HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý tất cả các exception không mong muốn khác.
     * Đây là "chốt chặn" cuối cùng.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUncaughtException(Exception ex) {
        // Ghi log ở mức ERROR với đầy đủ stack trace để dễ dàng debug
        logger.error("An unexpected error occurred", ex);
        return ResponseFactory.error(ErrorCodes.UNEXPECTED_ERROR, "Lỗi hệ thống, vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}