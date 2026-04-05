package com.example.coffeeorder.domain.order.facade;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.service.CoffeeService;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.order.dto.OrderResponse;
import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.event.OrderCreatedEvent;
import com.example.coffeeorder.domain.order.service.OrderService;
import com.example.coffeeorder.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final CoffeeService coffeeService;
    private final PointService pointService;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse order(OrderRequest request) {
        // 1. 메뉴 조회
        Coffee coffee = coffeeService.getById(request.getCoffeeId());

        // 2. 포인트 차감
        pointService.usePoint(request.getUserId(), Long.valueOf(coffee.getPrice()));

        // 3. 주문 저장
        Order order = orderService.saveOrder(request.getUserId(), coffee.getId(), coffee.getPrice());

        // 4. 주문 완료 이벤트 발행 (비동기 처리 대상)
        eventPublisher.publishEvent(new OrderCreatedEvent(order));

        return OrderResponse.from(order);
    }
}
