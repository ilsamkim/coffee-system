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
import org.springframework.test.util.ReflectionTestUtils;

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
        List<Long> popularCoffeeIds = List.of(3L, 1L, 2L); // 3번이 주문량 1위라고 가정
        given(orderService.getPopularCoffeeIds(any(LocalDateTime.class), eq(3)))
                .willReturn(popularCoffeeIds);

        Coffee coffee1 = Coffee.create("아메리카노", 4500, 100);
        ReflectionTestUtils.setField(coffee1, "id", 1L);
        Coffee coffee2 = Coffee.create("카페라떼", 5000, 100);
        ReflectionTestUtils.setField(coffee2, "id", 2L);
        Coffee coffee3 = Coffee.create("바닐라라떼", 5500, 100);
        ReflectionTestUtils.setField(coffee3, "id", 3L);

        // 서비스는 id 순서와 상관없이 리스트를 반환할 수 있음 (N+1 방지 쿼리 결과)
        given(coffeeService.findAllByIds(popularCoffeeIds))
                .willReturn(List.of(coffee1, coffee2, coffee3));

        // when
        List<CoffeeResponse> result = coffeeFacade.getPopularMenus();

        // then
        assertThat(result).hasSize(3);
        // 주문량이 많은 순서(3번 -> 1번 -> 2번)대로 정렬되어야 함
        assertThat(result.get(0).getName()).isEqualTo("바닐라라떼");
        assertThat(result.get(1).getName()).isEqualTo("아메리카노");
        assertThat(result.get(2).getName()).isEqualTo("카페라떼");
    }
}