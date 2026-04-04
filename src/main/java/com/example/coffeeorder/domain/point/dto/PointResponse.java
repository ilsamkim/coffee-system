package com.example.coffeeorder.domain.point.dto;

import com.example.coffeeorder.domain.point.entity.Point;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointResponse {
    private String userId;
    private Long amount;

    public static PointResponse from(Point point) {
        PointResponse response = new PointResponse();
        response.userId = point.getUserId();
        response.amount = point.getAmount();
        return response;
    }
}
