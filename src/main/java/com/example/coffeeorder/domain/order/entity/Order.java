package com.example.coffeeorder.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long coffeeId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate;

    public static Order create(String userId, Long coffeeId, Integer amount) {
        Order order = new Order();
        order.userId = userId;
        order.coffeeId = coffeeId;
        order.amount = amount;
        order.orderDate = LocalDateTime.now();
        return order;
    }
}
