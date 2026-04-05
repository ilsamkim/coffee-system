package com.example.coffeeorder.common.response;

import com.example.coffeeorder.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.code = "SUCCESS";
        response.message = "성공";
        response.data = data;
        return response;
    }

    public static ApiResponse<?> error(ErrorCode errorCode) {
        ApiResponse<?> response = new ApiResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = errorCode.getMessage();
        return response;
    }

    public static ApiResponse<?> error(ErrorCode errorCode, String message) {
        ApiResponse<?> response = new ApiResponse<>();
        response.success = false;
        response.code = errorCode.getCode();
        response.message = message;
        return response;
    }
}
