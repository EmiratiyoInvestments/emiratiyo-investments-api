package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.exception.AiAnalysisException;
import com.emiratiyo.api.exception.AiConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class GroqClient {

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Value("${emira.groq.api-key:}")
    private String apiKey;

    @Value("${emira.groq.model:llama-3.3-70b-versatile}")
    private String model;

    private final WebClient webClient;

    public Mono<String> generateContent(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new AiConfigurationException(
                    "Groq API key is not configured. Set EMIRA_GROQ_API_KEY."));
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        return webClient.post()
                .uri(GROQ_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.value() == 429, this::rateLimitError)
                .onStatus(HttpStatusCode::isError, this::apiError)
                .bodyToMono(JsonNode.class)
                .map(this::extractText);
    }

    private Mono<? extends Throwable> rateLimitError(ClientResponse response) {
        return response.releaseBody()
                .then(Mono.error(new AiAnalysisException(
                        "Groq rate limit exceeded. Please try again later.")));
    }

    private Mono<? extends Throwable> apiError(ClientResponse response) {
        HttpStatusCode status = response.statusCode();
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    log.warn("Groq API error {}: {}", status.value(), body);
                    return Mono.error(new AiAnalysisException(
                            "Groq request failed with status " + status.value()));
                });
    }

    private String extractText(JsonNode root) {
        String text = root.path("choices").path(0).path("message").path("content").asText();
        if (text == null || text.isEmpty()) {
            throw new AiAnalysisException("Groq returned empty text");
        }
        return text;
    }
}
