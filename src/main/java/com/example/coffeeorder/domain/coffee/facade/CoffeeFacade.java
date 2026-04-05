package com.example.coffeeorder.domain.coffee.facade;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoffeeFacade {

    private static final int POPULAR_MENU_DAYS = 7;
    private static final int POPULAR_MENU_LIMIT = 3;

    private final CoffeeService coffeeService;
    private final OrderService orderService;

    @Cacheable(value = "popularMenus")
    @Transactional(readOnly = true)
    public List<CoffeeResponse> getPopularMenus() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(POPULAR_MENU_DAYS);
        List<Long> coffeeIds = orderService.getPopularCoffeeIds(startDate, POPULAR_MENU_LIMIT);

        List<Coffee> coffees = coffeeService.findAllByIds(coffeeIds);

        return coffeeIds.stream()
                .map(id -> coffees.stream()
                        .filter(c -> c.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new ServiceErrorException(ErrorCode.ERR_MENU_NOT_FOUND)))
                .map(CoffeeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoffeeResponse> findAll() {
        return coffeeService.findAll();
    }
}
