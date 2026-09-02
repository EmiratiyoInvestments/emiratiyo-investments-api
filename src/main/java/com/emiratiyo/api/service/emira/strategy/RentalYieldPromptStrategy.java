package com.emiratiyo.api.service.emira.strategy;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.PromptStrategy;
import org.springframework.stereotype.Component;

@Component
public class RentalYieldPromptStrategy implements PromptStrategy {

    @Override
    public EmiraAnalysisRequest.AnalysisType analysisType() {
        return EmiraAnalysisRequest.AnalysisType.RENTAL_YIELD;
    }

    @Override
    public String build(String area, String marketContext, String additionalContext) {
        return String.format("""
                You are Emira, an expert Dubai real estate analyst
                for Emiratiyo Investments. Based on the market data
                provided, provide a rental yield analysis for %s.

                Market Context: %s
                Additional User Context: %s

                Structure your response exactly like this:
                CURRENT AVG RENT: [AED/year for apartment]
                GROSS YIELD: [%% range]
                NET YIELD: [%% range after typical costs]
                1 YEAR RENTAL INCOME: [on AED 2M property]
                3 YEAR RENTAL INCOME: [cumulative estimate]
                5 YEAR RENTAL INCOME: [cumulative estimate]
                RENTAL DEMAND OUTLOOK: [2-3 sentences]
                BEST PROPERTY TYPE: [apartment/villa + reason]
                DISCLAIMER: Estimates based on current market
                data and historical Dubai rental trends.
                """, area, marketContext, additionalContext);
    }
}
