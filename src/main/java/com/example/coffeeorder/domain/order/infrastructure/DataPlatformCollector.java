package com.example.coffeeorder.domain.order.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformCollector {

    /**
     * 실시간 데이터 수집 플랫폼으로 주문 내역을 전송합니다.
     * 실제 구현 시에는 Kafka, 외부 API 등을 호출하게 됩니다.
     */
    public void sendOrderInfo(String userId, Long coffeeId, Integer quantity, Long totalPrice) {
        log.info("데이터 수집 플랫폼 전송 - 사용자: {}, 메뉴: {}, 수량: {}, 결제금액: {}", userId, coffeeId, quantity, totalPrice);
        // TODO: 실제 전송 로직 (RestTemplates, WebClient, Kafka 등)
    }
}
