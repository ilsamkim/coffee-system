package com.example.coffeeorder.domain.order.controller;

import com.example.coffeeorder.common.response.ApiResponse;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.order.dto.OrderResponse;
import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.facade.OrderFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderFacade orderFacade;

    @InjectMocks
    private OrderController orderController;

    @Test
    @DisplayName("커피 주문 API를 호출한다.")
    void order() {
        // given
        String userId = "user123";
        Long coffeeId = 1L;
        Integer quantity = 1;
        Long totalPrice = 4500L;
        OrderRequest request = new OrderRequest(userId, coffeeId, quantity);
        
        Order order = Order.create(userId, coffeeId, quantity, totalPrice);
        given(orderFacade.order(any(OrderRequest.class))).willReturn(OrderResponse.from(order));

        // when
        ApiResponse<OrderResponse> response = orderController.order(request);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getUserId()).isEqualTo(userId);
        assertThat(response.getData().getCoffeeId()).isEqualTo(coffeeId);
        assertThat(response.getData().getQuantity()).isEqualTo(quantity);
        assertThat(response.getData().getTotalPrice()).isEqualTo(totalPrice);
    }
}
