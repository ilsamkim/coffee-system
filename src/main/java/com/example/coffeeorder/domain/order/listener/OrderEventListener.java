package com.example.coffeeorder.domain.order.listener;

import com.example.coffeeorder.domain.order.entity.Order;
import com.example.coffeeorder.domain.order.entity.OrderHistory;
import com.example.coffeeorder.domain.order.event.OrderCreatedEvent;
import com.example.coffeeorder.domain.order.infrastructure.DataPlatformCollector;
import com.example.coffeeorder.domain.order.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final DataPlatformCollector dataPlatformCollector;
    private final OrderHistoryRepository orderHistoryRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Order order = event.getOrder();
        log.info("비동기 주문 데이터 전송 시작 - 주문번호: {}", order.getId());

        try {
            dataPlatformCollector.sendOrderInfo(order.getUserId(), order.getCoffeeId(), order.getAmount());
            log.info("비동기 주문 데이터 전송 완료 - 주문번호: {}", order.getId());
        } catch (Exception e) {
            log.error("데이터 수집 플랫폼 전송 실패 - 주문번호: {}, 사유: {}", order.getId(), e.getMessage());
            // 실패 내역 저장 (나중에 스케줄러가 재시도)
            OrderHistory history = OrderHistory.create(order.getId(), order.getUserId(), order.getCoffeeId(), order.getAmount());
            history.fail();
            orderHistoryRepository.save(history);
        }
    }
}
