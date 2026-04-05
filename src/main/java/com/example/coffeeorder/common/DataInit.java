package com.example.coffeeorder.common;

import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final CoffeeRepository coffeeRepository;

    @Override
    public void run(String... args) {
        if (coffeeRepository.count() == 0) {
            coffeeRepository.saveAll(List.of(
                    Coffee.create("아메리카노", 4500),
                    Coffee.create("카페라떼", 5000),
                    Coffee.create("바닐라라떼", 5500),
                    Coffee.create("카라멜마끼아또", 6000),
                    Coffee.create("카페모카", 5500),
                    Coffee.create("콜드브루", 4800),
                    Coffee.create("에스프레소", 3500),
                    Coffee.create("아인슈페너", 6500),
                    Coffee.create("돌체라떼", 5800),
                    Coffee.create("자몽허니블랙티", 6300)
            ));
        }
    }
}
