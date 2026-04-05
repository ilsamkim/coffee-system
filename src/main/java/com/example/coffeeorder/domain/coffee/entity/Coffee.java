package com.example.coffeeorder.domain.coffee.entity;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "coffees")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    public static Coffee create(String name, Integer price, Integer stock) {
        Coffee coffee = new Coffee();
        coffee.name = name;
        coffee.price = price;
        coffee.stock = stock;
        return coffee;
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new ServiceErrorException(ErrorCode.ERR_OUT_OF_STOCK);
        }
        this.stock -= quantity;
    }
}
