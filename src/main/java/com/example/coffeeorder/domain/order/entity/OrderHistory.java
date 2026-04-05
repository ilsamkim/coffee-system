package com.example.coffeeorder.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long coffeeId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryStatus status;

    @Column(nullable = false)
    private int retryCount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum HistoryStatus {
        PENDING, SUCCESS, FAIL
    }

    private OrderHistory(Long orderId, String userId, Long coffeeId, Integer quantity, Long totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.coffeeId = coffeeId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = HistoryStatus.PENDING;
        this.retryCount = 0;
    }

    public static OrderHistory create(Long orderId, String userId, Long coffeeId, Integer quantity, Long totalPrice) {
        return new OrderHistory(orderId, userId, coffeeId, quantity, totalPrice);
    }

    public void complete() {
        this.status = HistoryStatus.SUCCESS;
    }

    public void fail() {
        this.status = HistoryStatus.FAIL;
        this.retryCount++;
    }
}
