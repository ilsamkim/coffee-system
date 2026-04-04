package com.example.coffeeorder.common.exception;

import lombok.Getter;

@Getter
public class ServiceErrorException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServiceErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
