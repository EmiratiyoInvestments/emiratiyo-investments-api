package com.emiratiyo.api.service;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.entity.EmiraAnalysisEntity;
import com.emiratiyo.api.exception.AiAnalysisException;
import com.emiratiyo.api.service.emira.AiClient;
import com.emiratiyo.api.service.emira.EmiraPromptBuilder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmiraService {

    private final EmiraHistoryService emiraHistoryService;
    private final EmiraPromptBuilder emiraPromptBuilder;
    private final AiClient aiClient;
   
    @CircuitBreaker(name = "emiraGemini", fallbackMethod = "fallbackAnalyse")
    public Mono<String> analyse(EmiraAnalysisRequest request) {
        log.info("=== EMIRA ANALYSE CALLED === type: {}, area: {}",
                request.analysisType(), request.area());

        String prompt = emiraPromptBuilder.buildPrompt(request);

        return aiClient.generateContent(prompt)
            .doOnSuccess(text -> {
                log.info("Gemini response received, length: {}", text.length());
                // Save to history asynchronously
                emiraHistoryService.save(
                        EmiraAnalysisEntity.builder()
                                .area(request.area())
                                .analysisType(request.analysisType().toString())
                                .responseText(text)
                                .build());
            })
            .doOnError(e -> log.error("Both Gemini keys failed or unexpected error: {}", e.getMessage()));
    }

    /**
     * Fallback method for Circuit Breaker
     */
    private Mono<String> fallbackAnalyse(EmiraAnalysisRequest request, Throwable t) {
        log.error("Circuit breaker opened for Emira Gemini: {}", t.getMessage());
        return Mono.error(new AiAnalysisException("Emira is temporarily unavailable. Please try again later."));
    }
}
