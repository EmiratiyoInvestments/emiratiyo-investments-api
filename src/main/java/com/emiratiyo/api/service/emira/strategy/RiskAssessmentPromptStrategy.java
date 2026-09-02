package com.emiratiyo.api.service.emira.strategy;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;
import com.emiratiyo.api.service.emira.PromptStrategy;
import org.springframework.stereotype.Component;

@Component
public class RiskAssessmentPromptStrategy implements PromptStrategy {

    @Override
    public EmiraAnalysisRequest.AnalysisType analysisType() {
        return EmiraAnalysisRequest.AnalysisType.RISK_ASSESSMENT;
    }

    @Override
    public String build(String area, String marketContext, String additionalContext) {
        return String.format("""
                You are Emira, an expert Dubai real estate analyst
                for Emiratiyo Investments. Provide a risk assessment
                for investing in %s right now.

                Market Context: %s
                Additional User Context: %s

                Structure your response exactly like this:
                RISK LEVEL: [Low/Medium/High]
                OVERSUPPLY RISK: [assessment + evidence]
                MACRO RISKS: [global factors that could impact]
                LIQUIDITY RISK: [how easy to sell if needed]
                REGULATORY RISK: [any UAE policy considerations]
                MARKET TIMING: [good time to buy or wait?]
                RISK SUMMARY: [2-3 sentences]
                """, area, marketContext, additionalContext);
    }
}
