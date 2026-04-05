package com.example.coffeeorder.domain.order.facade;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.repository.CoffeeRepository;
import com.example.coffeeorder.domain.order.dto.OrderRequest;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.repository.PointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OrderConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private com.example.coffeeorder.domain.order.repository.OrderRepository orderRepository;

    private Long coffeeId;
    private final String userId = "testUser";

    @BeforeEach
    void setUp() {
        Coffee coffee = coffeeRepository.save(Coffee.create("아메리카노", 100, 100));
        coffeeId = coffee.getId();
        pointRepository.save(Point.create(userId, 100000L));
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        coffeeRepository.deleteAll();
        pointRepository.deleteAll();
    }

    @Test
    @DisplayName("100건의 동시 주문 시 재고가 0이 되어야 한다")
    void orderConcurrencyTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        OrderRequest request = new OrderRequest(userId, coffeeId);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderFacade.order(request);
                } catch (Exception e) {
                    System.out.println("Order failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Coffee coffee = coffeeRepository.findById(coffeeId).orElseThrow();
        assertThat(coffee.getStock()).isEqualTo(0);
    }
}