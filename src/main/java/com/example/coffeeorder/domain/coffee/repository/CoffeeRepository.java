package com.example.coffeeorder.domain.coffee.repository;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}
