package com.emiratiyo.api.service.emira.strategy;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.PromptStrategy;
import org.springframework.stereotype.Component;

@Component
public class PriceForecastPromptStrategy implements PromptStrategy {

    @Override
    public EmiraAnalysisRequest.AnalysisType analysisType() {
        return EmiraAnalysisRequest.AnalysisType.PRICE_FORECAST;
    }

    @Override
    public String build(String area, String marketContext, String additionalContext) {
        return String.format("""
                You are Emira, an expert Dubai real estate analyst
                for Emiratiyo Investments. Based on the market data
                provided and your knowledge of Dubai real estate,
                provide a price forecast for %s.

                Market Context: %s
                Additional User Context: %s

                Structure your response exactly like this:
                CURRENT PRICE: [from market data]
                1 YEAR FORECAST: [price range AED/sqft + %% change]
                3 YEAR FORECAST: [price range AED/sqft + %% change]
                5 YEAR FORECAST: [price range AED/sqft + %% change]
                KEY DRIVERS: [3-4 bullet points]
                CONFIDENCE: [Low/Medium/High + one line reason]
                DISCLAIMER: This is an AI-assisted projection
                based on available data, not a financial guarantee.
                """, area, marketContext, additionalContext);
    }
}
