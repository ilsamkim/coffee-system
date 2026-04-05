package com.example.coffeeorder.domain.point.controller;

import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    @Test
    @DisplayName("포인트를 충전한다.")
    void charge() {
        // given
        String userId = "user123";
        Long amount = 1000L;
        PointRequest request = new PointRequest(userId, amount);
        
        PointResponse response = PointResponse.from(Point.create(userId, amount));
        given(pointService.charge(any(PointRequest.class))).willReturn(response);

        // when
        PointResponse result = pointController.charge(request);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAmount()).isEqualTo(amount);
    }
}
