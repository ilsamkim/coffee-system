package com.example.coffeeorder.domain.point.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

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
    }
}
