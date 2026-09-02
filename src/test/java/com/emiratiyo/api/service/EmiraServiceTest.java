package com.emiratiyo.api.service;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.entity.EmiraAnalysisEntity;
import com.emiratiyo.api.service.emira.AiClient;
import com.emiratiyo.api.service.emira.EmiraPromptBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.emiratiyo.api.dto.EmiraAnalysisRequest.AnalysisType.MARKET_PULSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmiraServiceTest {

    @Mock
    private EmiraHistoryService emiraHistoryService;

    @Mock
    private EmiraPromptBuilder emiraPromptBuilder;

    @Mock
    private AiClient aiClient;

    @InjectMocks
    private EmiraService emiraService;

    @Test
    void analyseBuildsPromptCallsGeminiAndSavesHistory() {
        EmiraAnalysisRequest request = EmiraAnalysisRequest.builder()
                .area("Downtown Dubai")
                .analysisType(MARKET_PULSE)
                .marketContext("Active transaction volume")
                .additionalContext("Luxury focus")
                .build();

        when(emiraPromptBuilder.buildPrompt(request)).thenReturn("prompt");
        when(aiClient.generateContent("prompt")).thenReturn(Mono.just("analysis-result"));

        StepVerifier.create(emiraService.analyse(request))
                .expectNext("analysis-result")
                .verifyComplete();

        verify(emiraPromptBuilder).buildPrompt(request);
        verify(aiClient).generateContent("prompt");

        ArgumentCaptor<EmiraAnalysisEntity> savedCaptor = ArgumentCaptor.forClass(EmiraAnalysisEntity.class);
        verify(emiraHistoryService).save(savedCaptor.capture());

        EmiraAnalysisEntity saved = savedCaptor.getValue();
        assertThat(saved.getArea()).isEqualTo("Downtown Dubai");
        assertThat(saved.getAnalysisType()).isEqualTo("MARKET_PULSE");
        assertThat(saved.getResponseText()).isEqualTo("analysis-result");
    }
}
