package com.example.coffeeorder.domain.point.controller;

import com.example.coffeeorder.common.response.ApiResponse;
import com.example.coffeeorder.domain.point.dto.PointRequest;
import com.example.coffeeorder.domain.point.dto.PointResponse;
import com.example.coffeeorder.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ApiResponse<PointResponse> charge(@RequestBody PointRequest request) {
        return ApiResponse.success(pointService.charge(request));
    }
}
