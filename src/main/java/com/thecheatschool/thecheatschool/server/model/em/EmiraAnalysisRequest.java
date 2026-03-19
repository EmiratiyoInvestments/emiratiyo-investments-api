package com.thecheatschool.thecheatschool.server.model.em;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmiraAnalysisRequest {
    private String area;
    private AnalysisType analysisType;
    private String marketContext;
    private String additionalContext;

    public enum AnalysisType {
        PRICE_FORECAST,
        RENTAL_YIELD,
        GROWTH_DRIVERS,
        RISK_ASSESSMENT,
        MARKET_PULSE
    }
}
