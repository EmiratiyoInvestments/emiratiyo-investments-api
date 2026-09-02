package com.emiratiyo.api.service.emira;

import com.emiratiyo.api.dto.EmiraAnalysisRequest;

public interface PromptStrategy {

    EmiraAnalysisRequest.AnalysisType analysisType();

    String build(String area, String marketContext, String additionalContext);
}
