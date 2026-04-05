package com.example.coffeeorder.domain.order.service;

import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 내역을 저장한다.")
    void saveOrder() {
        // given
        String userId = "user123";
        Long coffeeId = 1L;
        Integer quantity = 2;
        Long totalPrice = 10000L;
        Order order = Order.create(userId, coffeeId, quantity, totalPrice);
        ReflectionTestUtils.setField(order, "id", 1L);

        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Order result = orderService.saveOrder(userId, coffeeId, quantity, totalPrice);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCoffeeId()).isEqualTo(coffeeId);
        assertThat(result.getQuantity()).isEqualTo(quantity);
        assertThat(result.getTotalPrice()).isEqualTo(totalPrice);
    }
}
