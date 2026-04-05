package com.example.coffeeorder.domain.coffee.facade;

import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CoffeeFacadeTest {

    @Mock
    private CoffeeService coffeeService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    private CoffeeFacade coffeeFacade;

    @Test
    @DisplayName("최근 7일간 인기 메뉴 3개를 조회한다.")
    void getPopularMenus() {
        // given
        List<Long> popularCoffeeIds = List.of(1L, 2L, 3L);
        given(orderService.getPopularCoffeeIds(any(LocalDateTime.class), eq(3)))
                .willReturn(popularCoffeeIds);

        given(coffeeService.getById(1L)).willReturn(Coffee.create("아메리카노", 4500, 100));
        given(coffeeService.getById(2L)).willReturn(Coffee.create("카페라떼", 5000, 100));
        given(coffeeService.getById(3L)).willReturn(Coffee.create("바닐라라떼", 5500, 100));

        // when
        List<CoffeeResponse> result = coffeeFacade.getPopularMenus();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(1).getName()).isEqualTo("카페라떼");
        assertThat(result.get(2).getName()).isEqualTo("바닐라라떼");
    }
}