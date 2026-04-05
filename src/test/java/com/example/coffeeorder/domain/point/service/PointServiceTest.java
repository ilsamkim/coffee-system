package com.example.coffeeorder.domain.point.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private RLock lock;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void setUp() throws InterruptedException {
        given(redissonClient.getLock(anyString())).willReturn(lock);
        given(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
        given(lock.isHeldByCurrentThread()).willReturn(true);

        TransactionStatus status = new SimpleTransactionStatus();
        given(transactionManager.getTransaction(any())).willReturn(status);
    }

    @Test
    @DisplayName("새로운 사용자가 포인트를 충전한다.")
    void chargeFirstTime() {
        // given
        String userId = "user123";
        Long amount = 1000L;
        PointRequest request = new PointRequest(userId, amount);
        
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(pointRepository.save(any(Point.class))).willReturn(Point.create(userId, 0L));

        // when
        PointResponse response = pointService.charge(request);

        // then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getAmount()).isEqualTo(amount);
        verify(lock).unlock();
    }

    @Test
    @DisplayName("포인트 사용 시 잔액이 부족하면 예외가 발생한다.")
    void usePointInsufficient() {
        // given
        String userId = "user123";
        Point point = Point.create(userId, 500L);
        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(userId, 1000L))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessage(ErrorCode.ERR_INSUFFICIENT_POINTS.getMessage());
        verify(lock).unlock();
    }
}
