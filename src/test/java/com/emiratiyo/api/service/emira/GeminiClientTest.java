package com.emiratiyo.api.service.emira;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class GeminiClientTest {

    @Test
    void generateContentFallsBackToBackupKeyWhenPrimaryFails() {
        List<String> requestedUrls = new ArrayList<>();
        AtomicInteger callCount = new AtomicInteger();

        ExchangeFunction exchangeFunction = request -> {
            requestedUrls.add(request.url().toString());

            if (callCount.getAndIncrement() == 0) {
                return Mono.error(new RuntimeException("primary failed"));
            }

            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body("""
                            {"candidates":[{"content":{"parts":[{"text":"backup-response"}]}}]}
                            """)
                    .build());
        };

        GeminiClient geminiClient = new GeminiClient(WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build());
        ReflectionTestUtils.setField(geminiClient, "primaryKey", "primary-key");
        ReflectionTestUtils.setField(geminiClient, "backupKey", "backup-key");

        StepVerifier.create(geminiClient.generateContent("prompt"))
                .expectNext("backup-response")
                .verifyComplete();

        assertThat(requestedUrls).hasSize(2);
        assertThat(requestedUrls.get(0)).contains("key=primary-key");
        assertThat(requestedUrls.get(1)).contains("key=backup-key");
    }
}
