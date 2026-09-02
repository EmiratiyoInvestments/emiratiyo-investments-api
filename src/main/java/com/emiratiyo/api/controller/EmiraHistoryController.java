package com.emiratiyo.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emiratiyo.api.dto.EmiraHistoryResponse;
import com.emiratiyo.api.entity.EmiraAnalysisEntity;
import com.emiratiyo.api.service.EmiraHistoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/emira/history")
@RequiredArgsConstructor
public class EmiraHistoryController {

    private final EmiraHistoryService emiraHistoryService;

    @GetMapping
    public ResponseEntity<List<EmiraHistoryResponse>> getHistory() {
        return ResponseEntity.ok(emiraHistoryService.getHistory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmiraAnalysisEntity> getAnalysisById(@PathVariable Long id) {
        return ResponseEntity.ok(emiraHistoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable Long id) {
        if (!emiraHistoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        emiraHistoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
