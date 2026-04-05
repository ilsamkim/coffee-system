package com.example.coffeeorder.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private String userId;
    private Long coffeeId;
    private Integer amount;

    public OrderRequest(String userId, Long coffeeId, Integer amount) {
        this.userId = userId;
        this.coffeeId = coffeeId;
        this.amount = amount;
    }
}
