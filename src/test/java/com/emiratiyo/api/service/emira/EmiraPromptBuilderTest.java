package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.strategy.GrowthDriversPromptStrategy;
import com.emiratiyo.api.service.emira.strategy.MarketPulsePromptStrategy;
import com.emiratiyo.api.service.emira.strategy.PriceForecastPromptStrategy;
import com.emiratiyo.api.service.emira.strategy.RentalYieldPromptStrategy;
import com.emiratiyo.api.service.emira.strategy.RiskAssessmentPromptStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.emiratiyo.api.dto.EmiraAnalysisRequest.AnalysisType.PRICE_FORECAST;
import static org.assertj.core.api.Assertions.assertThat;

class EmiraPromptBuilderTest {

    private EmiraPromptBuilder emiraPromptBuilder;

    @BeforeEach
    void setUp() {
        emiraPromptBuilder = new EmiraPromptBuilder(List.of(
                new PriceForecastPromptStrategy(),
                new RentalYieldPromptStrategy(),
                new GrowthDriversPromptStrategy(),
                new RiskAssessmentPromptStrategy(),
                new MarketPulsePromptStrategy()));
    }

    @Test
    void buildPromptIncludesAreaAndContext() {
        EmiraAnalysisRequest request = EmiraAnalysisRequest.builder()
                .area("Dubai Marina")
                .analysisType(PRICE_FORECAST)
                .marketContext("Transactions are rising")
                .additionalContext("Focus on waterfront units")
                .build();

        String prompt = emiraPromptBuilder.buildPrompt(request);

        assertThat(prompt).contains("price forecast for Dubai Marina");
        assertThat(prompt).contains("Market Context: Transactions are rising");
        assertThat(prompt).contains("Additional User Context: Focus on waterfront units");
        assertThat(prompt).contains("CURRENT PRICE:");
        assertThat(prompt).contains("DISCLAIMER:");
    }

    @Test
    void buildPromptDefaultsMissingAdditionalContextToEmptyString() {
        EmiraAnalysisRequest request = EmiraAnalysisRequest.builder()
                .area("JVC")
                .analysisType(PRICE_FORECAST)
                .marketContext("Steady demand")
                .additionalContext(null)
                .build();

        String prompt = emiraPromptBuilder.buildPrompt(request);

        assertThat(prompt).contains("Additional User Context: ");
    }
}
