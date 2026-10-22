package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.exception.AiAnalysisException;
import com.emiratiyo.api.exception.AiConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GroqClientTest {

    @Test
    void generateContentReadsChatCompletionText() {
        GroqClient groqClient = client(ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("""
                        {"choices":[{"message":{"content":"groq-response"}}]}
                        """)
                .build());

        StepVerifier.create(groqClient.generateContent("prompt"))
                .expectNext("groq-response")
                .verifyComplete();
    }

    @Test
    void generateContentFailsWhenApiKeyIsMissing() {
        GroqClient groqClient = client(ClientResponse.create(HttpStatus.OK).build());
        ReflectionTestUtils.setField(groqClient, "apiKey", "");

        StepVerifier.create(groqClient.generateContent("prompt"))
                .expectError(AiConfigurationException.class)
                .verify();
    }

    @Test
    void generateContentMapsRateLimitWithoutRetry() {
        GroqClient groqClient = client(ClientResponse.create(HttpStatus.TOO_MANY_REQUESTS)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body("{\"error\":{\"message\":\"rate limited\"}}")
                .build());

        StepVerifier.create(groqClient.generateContent("prompt"))
                .expectErrorMatches(error ->
                        error instanceof AiAnalysisException
                                && error.getMessage().contains("rate limit"))
                .verify();
    }

    private GroqClient client(ClientResponse response) {
        GroqClient groqClient = new GroqClient(WebClient.builder()
                .exchangeFunction(request -> Mono.just(response))
                .build());
        ReflectionTestUtils.setField(groqClient, "apiKey", "test-key");
        ReflectionTestUtils.setField(groqClient, "model", "llama-3.3-70b-versatile");
        return groqClient;
    }
}
