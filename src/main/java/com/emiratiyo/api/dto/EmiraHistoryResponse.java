package com.emiratiyo.api.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record EmiraHistoryResponse(
    Long id,
    String area,
    String analysisType,
    LocalDateTime createdAt
) {}
