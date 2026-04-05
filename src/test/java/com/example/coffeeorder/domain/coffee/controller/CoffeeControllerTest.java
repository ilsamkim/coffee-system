package com.example.coffeeorder.domain.coffee.controller;

import com.example.coffeeorder.common.response.ApiResponse;
import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.facade.CoffeeFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CoffeeControllerTest {

    @Mock
    private CoffeeFacade coffeeFacade;

    @InjectMocks
    private CoffeeController coffeeController;

    @Test
    @DisplayName("커피 메뉴 목록을 조회한다.")
    void getCoffees() {
        // given
        List<CoffeeResponse> mockResponses = List.of(
                CoffeeResponse.from(Coffee.create("아메리카노", 4500)),
                CoffeeResponse.from(Coffee.create("카페라떼", 5000))
        );
        given(coffeeFacade.findAll()).willReturn(mockResponses);

        // when
        ApiResponse<List<CoffeeResponse>> response = coffeeController.getCoffees();

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("최근 7일간 인기 메뉴 목록을 조회한다.")
    void getPopularMenus() {
        // given
        List<CoffeeResponse> mockResponses = List.of(
                CoffeeResponse.from(Coffee.create("아메리카노", 4500)),
                CoffeeResponse.from(Coffee.create("카페라떼", 5000)),
                CoffeeResponse.from(Coffee.create("바닐라라떼", 5500))
        );
        given(coffeeFacade.getPopularMenus()).willReturn(mockResponses);

        // when
        ApiResponse<List<CoffeeResponse>> response = coffeeController.getPopularMenus();

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(3);
        assertThat(response.getData().get(0).getName()).isEqualTo("아메리카노");
    }
}
