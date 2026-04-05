package com.example.coffeeorder.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Point
    ERR_INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "P001", "포인트 잔액이 부족합니다."),
    ERR_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "포인트 정보가 없는 사용자입니다."),

    // Coffee
    ERR_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "존재하지 않는 메뉴입니다."),

    // Common
    ERR_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
