package com.example.coffeeorder.domain.order.dto;

import com.example.coffeeorder.domain.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String userId;
    private Long coffeeId;
    private Integer quantity;
    private Long totalPrice;
    private LocalDateTime orderDate;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.orderId = order.getId();
        response.userId = order.getUserId();
        response.coffeeId = order.getCoffeeId();
        response.quantity = order.getQuantity();
        response.totalPrice = order.getTotalPrice();
        response.orderDate = order.getOrderDate();
        return response;
    }
}
