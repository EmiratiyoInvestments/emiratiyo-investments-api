package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmiraPromptBuilder {

    private final List<PromptStrategy> strategies;

    public String buildPrompt(EmiraAnalysisRequest request) {
        String additionalContext = request.additionalContext() != null ? request.additionalContext() : "";

        return strategies.stream()
                .filter(strategy -> strategy.analysisType() == request.analysisType())
                .findFirst()
                .map(strategy -> strategy.build(request.area(), request.marketContext(), additionalContext))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported analysis type: " + request.analysisType()));
    }
}
