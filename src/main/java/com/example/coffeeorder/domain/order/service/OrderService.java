package com.example.coffeeorder.domain.order.service;

import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order saveOrder(String userId, Long coffeeId, Integer quantity, Long totalPrice) {
        Order order = Order.create(userId, coffeeId, quantity, totalPrice);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Long> getPopularCoffeeIds(LocalDateTime startDate, int limit) {
        return orderRepository.findPopularCoffeeIds(startDate, PageRequest.of(0, limit));
    }
}
