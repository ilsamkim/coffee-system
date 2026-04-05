package com.example.coffeeorder.domain.point.entity;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private Long amount;

    public static Point create(String userId, Long amount) {
        Point point = new Point();
        point.userId = userId;
        point.amount = amount;
        return point;
    }

    public void addAmount(Long amount) {
        this.amount += amount;
    }

    public void deductAmount(Long amount) {
        if (this.amount < amount) {
            throw new ServiceErrorException(ErrorCode.ERR_INSUFFICIENT_POINTS);
        }
        this.amount -= amount;
    }
}
