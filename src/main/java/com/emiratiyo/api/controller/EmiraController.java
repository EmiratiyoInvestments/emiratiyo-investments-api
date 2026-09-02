package com.emiratiyo.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.emiratiyo.api.dto.ApiResponse;
import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.EmiraService;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/emira")
@RequiredArgsConstructor
public class EmiraController {

    private final EmiraService emiraService;

    @PostMapping(value = "/analyse")
    public Mono<ResponseEntity<ApiResponse<String>>> analyse(@RequestBody EmiraAnalysisRequest request) {
        return emiraService.analyse(request)
                .map(text -> ResponseEntity.ok(ApiResponse.success(text)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(503).body(ApiResponse.error(e.getMessage()))));
    }
}
