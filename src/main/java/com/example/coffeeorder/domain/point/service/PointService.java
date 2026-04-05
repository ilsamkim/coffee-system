package com.example.coffeeorder.domain.point.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.entity.Point;
import com.example.coffeeorder.domain.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class PointService {

    private final PointRepository pointRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public PointService(PointRepository pointRepository,
                        RedissonClient redissonClient,
                        PlatformTransactionManager transactionManager) {
        this.pointRepository = pointRepository;
        this.redissonClient = redissonClient;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public PointResponse charge(PointRequest request) {
        String lockKey = "coffee-order:user:" + request.getUserId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, -1, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("잠시 후 다시 시도해 주세요.");
            }

            return transactionTemplate.execute(status -> {
                Point point = pointRepository.findByUserId(request.getUserId())
                        .orElseGet(() -> pointRepository.save(Point.create(request.getUserId(), 0L)));

                point.addAmount(request.getAmount());
                return PointResponse.from(point);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void usePoint(String userId, Long amount) {
        String lockKey = "coffee-order:user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 주문 과정에서 이미 락을 획득했을 수도 있으므로 reentrant하게 동작함
            boolean acquired = lock.tryLock(5, -1, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("잠시 후 다시 시도해 주세요.");
            }

            transactionTemplate.executeWithoutResult(status -> {
                Point point = pointRepository.findByUserId(userId)
                        .orElseThrow(() -> new ServiceErrorException(ErrorCode.ERR_POINT_NOT_FOUND));
                point.deductAmount(amount);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
