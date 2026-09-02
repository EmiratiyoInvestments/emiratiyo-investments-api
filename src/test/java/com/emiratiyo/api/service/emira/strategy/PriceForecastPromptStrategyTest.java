package com.emiratiyo.api.service.emira.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceForecastPromptStrategyTest {

    private final PriceForecastPromptStrategy strategy = new PriceForecastPromptStrategy();

    @Test
    void buildsPriceForecastPrompt() {
        String prompt = strategy.build("Palm Jumeirah", "Strong demand", "Villa focus");

        assertThat(prompt).contains("price forecast for Palm Jumeirah");
        assertThat(prompt).contains("Market Context: Strong demand");
        assertThat(prompt).contains("Additional User Context: Villa focus");
        assertThat(strategy.analysisType().name()).isEqualTo("PRICE_FORECAST");
    }
}
