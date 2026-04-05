package com.example.coffeeorder.domain.coffee.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public CoffeeService(CoffeeRepository coffeeRepository,
                         RedissonClient redissonClient,
                         PlatformTransactionManager transactionManager) {
        this.coffeeRepository = coffeeRepository;
        this.redissonClient = redissonClient;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Cacheable(value = "coffees")
    @Transactional(readOnly = true)
    public List<CoffeeResponse> findAll() {
        return coffeeRepository.findAll().stream()
                .map(CoffeeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Coffee getById(Long id) {
        return coffeeRepository.findById(id)
                .orElseThrow(() -> new ServiceErrorException(ErrorCode.ERR_MENU_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Coffee> findAllByIds(List<Long> ids) {
        return coffeeRepository.findAllById(ids);
    }

    public void decreaseStock(Long id, int quantity) {
        String lockKey = "coffee-order:coffee:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, -1, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("잠시 후 다시 시도해 주세요.");
            }

            transactionTemplate.executeWithoutResult(status -> {
                Coffee coffee = getById(id);
                coffee.decreaseStock(quantity);
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
