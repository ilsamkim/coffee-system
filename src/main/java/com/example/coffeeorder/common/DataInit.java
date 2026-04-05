package com.example.coffeeorder.common;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.repository.CoffeeRepository;
import com.example.coffeeorder.domain.order.entity.OrderHistory;
import com.example.coffeeorder.domain.order.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final CoffeeRepository coffeeRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public void run(String... args) {
        // 커피 데이터 초기화
        if (coffeeRepository.count() == 0) {
            coffeeRepository.saveAll(List.of(
                    Coffee.create("아메리카노", 4500, 100),
                    Coffee.create("카페라떼", 5000, 100),
                    Coffee.create("바닐라라떼", 5500, 100),
                    Coffee.create("카라멜마끼아또", 6000, 100),
                    Coffee.create("카페모카", 5500, 100),
                    Coffee.create("콜드브루", 4800, 100),
                    Coffee.create("에스프레소", 3500, 100),
                    Coffee.create("아인슈페너", 6500, 100),
                    Coffee.create("돌체라떼", 5800, 100),
                    Coffee.create("자몽허니블랙티", 6300, 100)
            ));
        }

        if (orderHistoryRepository.count() == 0) {
            OrderHistory history1 = OrderHistory.create(101L, "user1", 1L, 4500);
            history1.fail();

            OrderHistory history2 = OrderHistory.create(102L, "user2", 2L, 5000);
            history2.fail();

            OrderHistory history3 = OrderHistory.create(103L, "user3", 3L, 5500);
            history3.fail();

            orderHistoryRepository.saveAll(List.of(history1, history2, history3));
        }
    }
}
