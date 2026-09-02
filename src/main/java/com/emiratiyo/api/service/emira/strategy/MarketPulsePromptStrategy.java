package com.emiratiyo.api.service.emira.strategy;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.PromptStrategy;
import org.springframework.stereotype.Component;

@Component
public class MarketPulsePromptStrategy implements PromptStrategy {

    @Override
    public EmiraAnalysisRequest.AnalysisType analysisType() {
        return EmiraAnalysisRequest.AnalysisType.MARKET_PULSE;
    }

    @Override
    public String build(String area, String marketContext, String additionalContext) {
        return String.format("""
                You are Emira, an expert Dubai real estate analyst
                for Emiratiyo Investments. Based on the transaction
                data provided, give a market pulse reading for %s.

                Market Context: %s
                Additional User Context: %s

                Structure your response exactly like this:
                TRANSACTION ACTIVITY: [volume vs Dubai average]
                PRICE TREND: [direction and momentum]
                OFF-PLAN VS READY: [split and what it signals]
                BUYER DEMAND: [strong/moderate/weak + reason]
                COMPARED TO DUBAI: [how area ranks overall]
                HOT OR NOT: [single word verdict + one line reason]
                """, area, marketContext, additionalContext);
    }
}
