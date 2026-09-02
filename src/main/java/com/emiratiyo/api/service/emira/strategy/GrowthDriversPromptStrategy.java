package com.emiratiyo.api.service.emira.strategy;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.PromptStrategy;
import org.springframework.stereotype.Component;

@Component
public class GrowthDriversPromptStrategy implements PromptStrategy {

    @Override
    public EmiraAnalysisRequest.AnalysisType analysisType() {
        return EmiraAnalysisRequest.AnalysisType.GROWTH_DRIVERS;
    }

    @Override
    public String build(String area, String marketContext, String additionalContext) {
        return String.format("""
                You are Emira, an expert Dubai real estate analyst
                for Emiratiyo Investments. Analyse the growth
                drivers for %s in Dubai.

                Market Context: %s
                Additional User Context: %s

                Structure your response exactly like this:
                OVERALL OUTLOOK: [Bullish/Neutral/Bearish + reason]
                GOVERNMENT INITIATIVES: [relevant UAE/Dubai projects]
                INFRASTRUCTURE: [transport, developments nearby]
                DEMAND FACTORS: [who is buying and why]
                SUPPLY PIPELINE: [new units coming, impact]
                MARKET MOMENTUM: [from transaction data provided]
                VERDICT: [2-3 sentence summary]
                """, area, marketContext, additionalContext);
    }
}
