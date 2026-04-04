package com.example.coffeeorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CoffeeOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeOrderApplication.class, args);
    }

}
