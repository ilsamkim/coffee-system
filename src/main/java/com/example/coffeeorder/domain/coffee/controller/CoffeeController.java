package com.example.coffeeorder.domain.coffee.controller;

import com.example.coffeeorder.common.response.ApiResponse;
import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.facade.CoffeeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coffees")
public class CoffeeController {

    private final CoffeeFacade coffeeFacade;

    @GetMapping
    public ApiResponse<List<CoffeeResponse>> getCoffees() {
        return ApiResponse.success(coffeeFacade.findAll());
    }

    @GetMapping("/popular")
    public ApiResponse<List<CoffeeResponse>> getPopularMenus() {
        return ApiResponse.success(coffeeFacade.getPopularMenus());
    }
}
