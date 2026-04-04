package com.example.coffeeorder.domain.order.facade;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.order.dto.OrderResponse;
import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.infrastructure.DataPlatformCollector;
import com.example.coffeeorder.domain.order.service.OrderService;
import com.example.coffeeorder.domain.point.service.PointService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private CoffeeService coffeeService;
    @Mock
    private PointService pointService;
    @Mock
    private OrderService orderService;
    @Mock
    private DataPlatformCollector dataPlatformCollector;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    @DisplayName("커피 주문 전체 프로세스를 성공적으로 수행한다.")
    void orderProcess() {
        // given
        String userId = "user123";
        Long coffeeId = 1L;
        Integer price = 5000;
        OrderRequest request = new OrderRequest(userId, coffeeId);

        Coffee coffee = Coffee.create("아메리카노", price);
        ReflectionTestUtils.setField(coffee, "id", coffeeId);

        Order order = Order.create(userId, coffeeId, price);
        ReflectionTestUtils.setField(order, "id", 100L);

        given(coffeeService.getById(coffeeId)).willReturn(coffee);
        given(orderService.saveOrder(userId, coffeeId, price)).willReturn(order);

        // when
        OrderResponse response = orderFacade.order(request);

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualTo(price);
        
        // 각 서비스 호출 확인
        verify(pointService).usePoint(userId, Long.valueOf(price));
        verify(orderService).saveOrder(userId, coffeeId, price);
        verify(dataPlatformCollector).sendOrderInfo(userId, coffeeId, price);
    }
}
