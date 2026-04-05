package com.example.coffeeorder.domain.coffee.service;

import com.example.coffeeorder.common.exception.ErrorCode;
import com.example.coffeeorder.common.exception.ServiceErrorException;
import com.example.coffeeorder.domain.coffee.dto.CoffeeResponse;
import com.example.coffeeorder.domain.coffee.entity.Coffee;
import com.example.coffeeorder.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeeService {

    private final CoffeeRepository coffeeRepository;

    @Transactional(readOnly = true)
    public List<CoffeeResponse> findAll() {
        return coffeeRepository.findAll().stream()
                .map(CoffeeResponse::from)
                .collect(Collectors.toList());
    }

    public Coffee getById(Long id) {
        return coffeeRepository.findById(id)
                .orElseThrow(() -> new ServiceErrorException(ErrorCode.ERR_MENU_NOT_FOUND));
    }
}
