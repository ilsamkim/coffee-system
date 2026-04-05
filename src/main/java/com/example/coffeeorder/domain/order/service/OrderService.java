package com.example.coffeeorder.domain.order.service;

import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order saveOrder(String userId, Long coffeeId, Integer price) {
        Order order = Order.create(userId, coffeeId, price);
        return orderRepository.save(order);
    }
}
