package com.example.coffeeorder.domain.point.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointRequest {
    private String userId;
    private Long amount;

    public PointRequest(String userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
