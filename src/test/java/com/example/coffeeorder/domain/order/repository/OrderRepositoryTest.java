package com.example.coffeeorder.domain.order.repository;

import com.example.coffeeorder.domain.order.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("최근 7일간 가장 많이 주문된 커피 ID 목록을 조회한다.")
    void findPopularCoffeeIds() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 1번 커피: 3번 주문 (7일 이내)
        orderRepository.save(createOrder("user1", 1L, 4500, now.minusDays(1)));
        orderRepository.save(createOrder("user2", 1L, 4500, now.minusDays(2)));
        orderRepository.save(createOrder("user3", 1L, 4500, now.minusDays(3)));

        // 2번 커피: 5번 주문 (7일 이내)
        orderRepository.save(createOrder("user4", 2L, 5000, now.minusDays(1)));
        orderRepository.save(createOrder("user5", 2L, 5000, now.minusDays(2)));
        orderRepository.save(createOrder("user6", 2L, 5000, now.minusDays(3)));
        orderRepository.save(createOrder("user7", 2L, 5000, now.minusDays(4)));
        orderRepository.save(createOrder("user8", 2L, 5000, now.minusDays(5)));

        // 3번 커피: 2번 주문 (7일 이내)
        orderRepository.save(createOrder("user9", 3L, 5500, now.minusDays(1)));
        orderRepository.save(createOrder("user10", 3L, 5500, now.minusDays(2)));

        // 4번 커피: 10번 주문 (하지만 8일 전 - 포함되지 않아야 함)
        orderRepository.save(createOrder("user11", 4L, 6000, now.minusDays(8)));

        // when
        List<Long> result = orderRepository.findPopularCoffeeIds(sevenDaysAgo, PageRequest.of(0, 3));

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(2L); // 5번 주문
        assertThat(result.get(1)).isEqualTo(1L); // 3번 주문
        assertThat(result.get(2)).isEqualTo(3L); // 2번 주문
        assertThat(result).doesNotContain(4L);   // 기간 외 데이터 미포함
    }

    private Order createOrder(String userId, Long coffeeId, Integer amount, LocalDateTime orderDate) {
        Order order = Order.create(userId, coffeeId, amount);
        ReflectionTestUtils.setField(order, "orderDate", orderDate);
        return order;
    }
}
