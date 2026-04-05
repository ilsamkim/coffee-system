package com.example.coffeeorder.domain.coffee.entity;

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

    public static Coffee create(
            String name, 
            Integer price) {
        Coffee coffee = new Coffee();
        coffee.name = name;
        coffee.price = price;
    
        return coffee;
    }
}
