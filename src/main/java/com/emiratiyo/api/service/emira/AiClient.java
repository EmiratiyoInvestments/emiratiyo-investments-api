package com.emiratiyo.api.service.emira;

import reactor.core.publisher.Mono;

public interface AiClient {

    Mono<String> generateContent(String prompt);
}
