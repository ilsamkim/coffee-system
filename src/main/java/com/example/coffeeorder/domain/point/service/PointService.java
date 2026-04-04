package com.example.coffeeorder.domain.point.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public PointResponse charge(PointRequest request) {
        Point point = pointRepository.findByUserId(request.getUserId())
                .orElseGet(() -> pointRepository.save(Point.create(request.getUserId(), 0L)));

        point.addAmount(request.getAmount());
        return PointResponse.from(point);
    }

    @Transactional
    public void usePoint(String userId, Long amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceErrorException(ErrorCode.ERR_POINT_NOT_FOUND));
        point.deductAmount(amount);
    }
}
