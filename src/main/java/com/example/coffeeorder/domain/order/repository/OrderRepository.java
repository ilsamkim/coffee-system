package com.example.coffeeorder.domain.order.repository;

import com.example.coffeeorder.domain.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o.coffeeId " +
           "FROM Order o " +
           "WHERE o.orderDate >= :startDate " +
           "GROUP BY o.coffeeId " +
           "ORDER BY COUNT(o) DESC")
    List<Long> findPopularCoffeeIds(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
