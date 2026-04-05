package com.example.coffeeorder.domain.coffee.dto;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE) // Jackson 역직렬화를 위한 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CoffeeResponse implements Serializable {

    private Long id;
    private String name;
    private Integer price;

    public static CoffeeResponse from(Coffee coffee) {
        return new CoffeeResponse(
                coffee.getId(),
                coffee.getName(),
                coffee.getPrice()
        );
    }
}
