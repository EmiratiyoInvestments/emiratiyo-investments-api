package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.exception.AiAnalysisException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GeminiClient implements AiClient {

    private static final String GEMINI_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s";

    @Value("${emira.gemini.primary-key:}")
    private String primaryKey;

    @Value("${emira.gemini.backup-key:}")
    private String backupKey;

    private final WebClient webClient;

    public Mono<String> generateContent(String prompt) {
        return callGemini(prompt, primaryKey)
                .onErrorResume(e -> {
                    log.warn("Primary key failed, trying backup key: {}", e.getMessage());
                    return callGemini(prompt, backupKey);
                });
    }

    private Mono<String> callGemini(String prompt, String key) {
        String url = String.format(GEMINI_URL_TEMPLATE, key);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractText);
    }

    private String extractText(JsonNode root) {
        String text = root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();

        if (text == null || text.isEmpty()) {
            throw new AiAnalysisException("Gemini returned empty text");
        }

        return text;
    }
}
