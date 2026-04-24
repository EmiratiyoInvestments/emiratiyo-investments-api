package com.thecheatschool.thecheatschool.server.controller.em;

import com.thecheatschool.thecheatschool.server.model.em.EmiraAnalysis;
import com.thecheatschool.thecheatschool.server.model.em.EmiraHistoryDto;
import com.thecheatschool.thecheatschool.server.service.em.EmiraHistoryService;
import com.thecheatschool.thecheatschool.server.util.EmiraAuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/internal/history")
@RequiredArgsConstructor
public class EmiraHistoryController {

    private final EmiraHistoryService emiraHistoryService;

    @Value("${emira.internal.secret}")
    private String internalSecret;

    @GetMapping
    public ResponseEntity<List<EmiraHistoryDto>> getHistory(
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey) {

        if (!EmiraAuthUtil.isAuthorized(internalKey, internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(emiraHistoryService.getHistory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmiraAnalysis> getAnalysisById(
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey,
            @PathVariable Long id) {

        if (!EmiraAuthUtil.isAuthorized(internalKey, internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        EmiraAnalysis analysis = emiraHistoryService.getById(id);
        return analysis != null
                ? ResponseEntity.ok(analysis)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(
            @RequestHeader(value = "X-Internal-Key", required = false) String internalKey,
            @PathVariable Long id) {

        if (!EmiraAuthUtil.isAuthorized(internalKey, internalSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!emiraHistoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        emiraHistoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}