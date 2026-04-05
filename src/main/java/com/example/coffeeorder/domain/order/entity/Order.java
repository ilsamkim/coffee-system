package com.example.coffeeorder.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime orderDate;

    public static Order create(String userId, Long coffeeId, Integer amount) {
        Order order = new Order();
        order.userId = userId;
        order.coffeeId = coffeeId;
        order.amount = amount;
        return order;
    }
}
