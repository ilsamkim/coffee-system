package com.example.coffeeorder.common.exception;

import com.example.coffeeorder.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceErrorException.class)
    protected ResponseEntity<ApiResponse<?>> handleServiceErrorException(ServiceErrorException e) {
        log.error("ServiceErrorException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ResponseEntity
                .status(ErrorCode.ERR_INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.ERR_INTERNAL_SERVER_ERROR));
    }
}
