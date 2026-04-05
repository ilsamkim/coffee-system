package com.example.coffeeorder.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private String userId;
    private Long coffeeId;

    public OrderRequest(String userId, Long coffeeId) {
        this.userId = userId;
        this.coffeeId = coffeeId;
    }
}
