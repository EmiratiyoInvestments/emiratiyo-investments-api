package com.thecheatschool.thecheatschool.server.controller.em;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecheatschool.thecheatschool.server.model.ApiResponse;
import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupRequest;
import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupSubmission;
import com.thecheatschool.thecheatschool.server.repository.em.EMBusinessSetupRepository;
import com.thecheatschool.thecheatschool.server.service.em.EMBusinessSetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/em/business-setup")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EMBusinessSetupController {

    private final EMBusinessSetupService businessSetupService;
    private final EMBusinessSetupRepository businessSetupRepository;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> info() {
        return ResponseEntity.ok(ApiResponse.success("Use POST with JSON body to submit the Emiratiyo Investments business setup form."));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> submit(@Valid @RequestBody EMBusinessSetupRequest request) {
        log.info("Received business setup submission from: {}", request.getEmail());
        try {
            businessSetupService.processBusinessSetup(request);
            return ResponseEntity.ok(ApiResponse.success("Thanks! We'll contact you soon about your business setup."));
        } catch (Exception e) {
            log.error("Error processing business setup", e);
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to submit. Please try again."));
        }
    }

    @GetMapping("/failed")
    public ResponseEntity<ApiResponse<List<EMBusinessSetupSubmission>>> getFailedSubmissions() {
        log.info("Fetching failed business setup submissions");
        List<EMBusinessSetupSubmission> failed = businessSetupRepository.findByStatus("EMAIL_FAILED");
        return ResponseEntity.ok(ApiResponse.success(failed));
    }

    //fallback
    @PostMapping(consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<ApiResponse<String>> submitFallback(@RequestBody String body) {
        try {
            EMBusinessSetupRequest request = objectMapper.readValue(body, EMBusinessSetupRequest.class);
            return submit(request);
        } catch (Exception ex) {
            log.error("Failed to parse EM business setup request body", ex);
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid request body. Please send JSON with application/json Content-Type."));
        }
    }
}