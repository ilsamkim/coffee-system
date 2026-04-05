package com.example.coffeeorder.domain.order.facade;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.order.dto.OrderResponse;
import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.event.OrderCreatedEvent;
import com.example.coffeeorder.domain.order.service.OrderService;
import com.example.coffeeorder.domain.point.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderFacade {

    private final CoffeeService coffeeService;
    private final PointService pointService;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public OrderFacade(CoffeeService coffeeService,
                       PointService pointService,
                       OrderService orderService,
                       ApplicationEventPublisher eventPublisher,
                       RedissonClient redissonClient,
                       PlatformTransactionManager transactionManager) {
        this.coffeeService = coffeeService;
        this.pointService = pointService;
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
        this.redissonClient = redissonClient;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public OrderResponse order(OrderRequest request) {
        String userLockKey = "coffee-order:user:" + request.getUserId();
        String coffeeLockKey = "coffee-order:coffee:" + request.getCoffeeId();

        RLock userLock = redissonClient.getLock(userLockKey);
        RLock coffeeLock = redissonClient.getLock(coffeeLockKey);
        RLock multiLock = redissonClient.getMultiLock(userLock, coffeeLock);

        try {
            // MultiLock을 사용하여 두 개의 락을 원자적으로 획득 시도
            // waitTime: 5초, leaseTime: -1 (watchdog 활성화)
            boolean lockAcquired = multiLock.tryLock(5, -1, TimeUnit.SECONDS);

            if (lockAcquired) {
                return transactionTemplate.execute(status -> createOrder(request));
            } else {
                throw new RuntimeException("잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생");
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

    private OrderResponse createOrder(OrderRequest request) {
        // 1. 메뉴 조회
        Coffee coffee = coffeeService.getById(request.getCoffeeId());

        // 2. 재고 차감
        coffeeService.decreaseStock(coffee.getId(), 1);

        // 3. 포인트 차감
        pointService.usePoint(request.getUserId(), Long.valueOf(coffee.getPrice()));

        // 4. 주문 저장
        Order order = orderService.saveOrder(request.getUserId(), coffee.getId(), coffee.getPrice());

        // 5. 주문 완료 이벤트 발행 (비동기 처리)
        eventPublisher.publishEvent(new OrderCreatedEvent(order));

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.example.coffeeorder.domain.coffee.dto.CoffeeResponse> findAll() {
        return coffeeService.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.List<com.example.coffeeorder.domain.coffee.dto.CoffeeResponse> getPopularMenus() {
        return coffeeService.findAll().subList(0, 3);
    }
}
