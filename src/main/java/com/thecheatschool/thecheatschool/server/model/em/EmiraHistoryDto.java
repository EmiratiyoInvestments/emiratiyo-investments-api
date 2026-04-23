package com.thecheatschool.thecheatschool.server.model.em;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Lightweight DTO for Emira analysis history list — excludes the full responseText
 * to keep list responses lean and Redis-efficient.
 */
@Data
@Builder
public class EmiraHistoryDto {

    private Long id;
    private String area;
    private String analysisType;
    private LocalDateTime createdAt;
}
