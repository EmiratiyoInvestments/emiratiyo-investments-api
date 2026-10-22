package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.exception.AiConfigurationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class RoutingAiClient implements AiClient {

    private final GroqClient groqClient;
    private final GeminiClient geminiClient;

    @Value("${emira.ai.provider:groq}")
    private String provider;

    @Override
    public Mono<String> generateContent(String prompt) {
        if (useGemini()) {
            log.debug("Using Gemini for Emira analysis");
            return geminiClient.generateContent(prompt);
        }

        log.debug("Using Groq for Emira analysis");
        return groqClient.generateContent(prompt)
                .onErrorResume(e -> {
                    if (e instanceof AiConfigurationException) {
                        return Mono.error(e);
                    }
                    log.warn("Groq failed, using Gemini backup: {}", e.getMessage());
                    return geminiClient.generateContent(prompt);
                });
    }

    private boolean useGemini() {
        return "gemini".equalsIgnoreCase(provider != null ? provider.trim() : "");
    }
}
