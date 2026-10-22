package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.exception.AiConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingAiClientTest {

    @Test
    void usesGroqWhenProviderIsGroq() {
        TrackingGroq groq = new TrackingGroq(Mono.just("from-groq"));
        TrackingGemini gemini = new TrackingGemini(Mono.just("from-gemini"));
        RoutingAiClient routing = routing(groq, gemini, "groq");

        StepVerifier.create(routing.generateContent("prompt"))
                .expectNext("from-groq")
                .verifyComplete();

        assertThat(gemini.called).isFalse();
    }

    @Test
    void usesGeminiWhenProviderIsGemini() {
        TrackingGroq groq = new TrackingGroq(Mono.just("from-groq"));
        TrackingGemini gemini = new TrackingGemini(Mono.just("from-gemini"));
        RoutingAiClient routing = routing(groq, gemini, "gemini");

        StepVerifier.create(routing.generateContent("prompt"))
                .expectNext("from-gemini")
                .verifyComplete();

        assertThat(groq.called).isFalse();
    }

    @Test
    void fallsBackToGeminiWhenGroqFails() {
        TrackingGroq groq = new TrackingGroq(Mono.error(new RuntimeException("rate limited")));
        TrackingGemini gemini = new TrackingGemini(Mono.just("from-gemini"));
        RoutingAiClient routing = routing(groq, gemini, "groq");

        StepVerifier.create(routing.generateContent("prompt"))
                .expectNext("from-gemini")
                .verifyComplete();
    }

    @Test
    void doesNotFallBackWhenGroqKeyIsMissing() {
        TrackingGroq groq = new TrackingGroq(Mono.error(new AiConfigurationException("missing key")));
        TrackingGemini gemini = new TrackingGemini(Mono.just("from-gemini"));
        RoutingAiClient routing = routing(groq, gemini, "groq");

        StepVerifier.create(routing.generateContent("prompt"))
                .expectError(AiConfigurationException.class)
                .verify();

        assertThat(gemini.called).isFalse();
    }

    private RoutingAiClient routing(GroqClient groq, GeminiClient gemini, String provider) {
        RoutingAiClient routing = new RoutingAiClient(groq, gemini);
        ReflectionTestUtils.setField(routing, "provider", provider);
        return routing;
    }

    private static class TrackingGroq extends GroqClient {
        private final Mono<String> result;
        private boolean called;

        TrackingGroq(Mono<String> result) {
            super(WebClient.builder().build());
            this.result = result;
        }

        @Override
        public Mono<String> generateContent(String prompt) {
            called = true;
            return result;
        }
    }

    private static class TrackingGemini extends GeminiClient {
        private final Mono<String> result;
        private boolean called;

        TrackingGemini(Mono<String> result) {
            super(WebClient.builder().build());
            this.result = result;
        }

        @Override
        public Mono<String> generateContent(String prompt) {
            called = true;
            return result;
        }
    }
}
