package com.example.coffeeorder.domain.coffee.dto;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import lombok.Getter;

@Getter
public class CoffeeResponse {

    private final Long id;
    private final String name;
    private final Integer price;

    private CoffeeResponse(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static CoffeeResponse from(Coffee coffee) {
        return new CoffeeResponse(
                coffee.getId(),
                coffee.getName(),
                coffee.getPrice()
        );
    }
}
