package com.example.coffeeorder.domain.order.facade;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.order.dto.OrderResponse;
import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.service.OrderService;
import com.example.coffeeorder.domain.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Spy;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private CoffeeService coffeeService;
    @Mock
    private PointService pointService;
    @Mock
    private OrderService orderService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private RLock lock;
    @Mock
    private RLock multiLock;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    @DisplayName("커피 주문 전체 프로세스를 성공적으로 수행한다.")
    void orderProcess() throws InterruptedException {
        // given
        String userId = "user123";
        Long coffeeId = 1L;
        Integer price = 5000;
        OrderRequest request = new OrderRequest(userId, coffeeId);

        Coffee coffee = Coffee.create("아메리카노", price, 100);
        ReflectionTestUtils.setField(coffee, "id", coffeeId);

        Order order = Order.create(userId, coffeeId, price);
        ReflectionTestUtils.setField(order, "id", 100L);

        // Redisson MultiLock 모킹
        // getLock이 두 번 호출됨 (userLockKey, coffeeLockKey)
        RLock userLock = mock(RLock.class);
        RLock coffeeLock = mock(RLock.class);
        given(redissonClient.getLock(anyString())).willReturn(userLock, coffeeLock);
        
        // MultiLock 모킹 (varargs 대응)
        given(redissonClient.getMultiLock(any(RLock.class), any(RLock.class))).willReturn(multiLock);
        given(multiLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
        given(multiLock.isHeldByCurrentThread()).willReturn(true);

        // TransactionManager 모킹
        TransactionStatus status = new SimpleTransactionStatus();
        given(transactionManager.getTransaction(any())).willReturn(status);

        given(coffeeService.getById(coffeeId)).willReturn(coffee);
        given(orderService.saveOrder(userId, coffeeId, price)).willReturn(order);

        // when
        OrderResponse response = orderFacade.order(request);

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualTo(price);
        
        verify(coffeeService).decreaseStock(coffeeId, 1);
        verify(pointService).usePoint(userId, Long.valueOf(price));
        verify(orderService).saveOrder(userId, coffeeId, price);
        verify(eventPublisher).publishEvent(any(com.example.coffeeorder.domain.order.event.OrderCreatedEvent.class));
        verify(multiLock).unlock();
    }
}