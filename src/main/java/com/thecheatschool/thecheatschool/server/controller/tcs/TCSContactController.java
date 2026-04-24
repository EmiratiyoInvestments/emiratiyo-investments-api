package com.thecheatschool.thecheatschool.server.controller.tcs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecheatschool.thecheatschool.server.model.ApiResponse;
import com.thecheatschool.thecheatschool.server.model.tcs.TCSContact;
import com.thecheatschool.thecheatschool.server.model.tcs.TCSContactRequest;
import com.thecheatschool.thecheatschool.server.repository.tcs.TCSContactRepository;
import com.thecheatschool.thecheatschool.server.service.tcs.TCSContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TCSContactController {

    private final TCSContactService contactService;
    private final TCSContactRepository contactRepository;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> contactInfo() {
        return ResponseEntity.ok(ApiResponse.success("Use POST with JSON body to submit the contact form."));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> submitContactForm(@Valid @RequestBody TCSContactRequest request) {
        log.info("Received contact form submission from: {}", request.getEmail());
        try {
            contactService.processContactForm(request);
            return ResponseEntity.ok(ApiResponse.success("Message sent successfully! We'll get back to you soon."));
        } catch (Exception e) {
            log.error("Error processing contact form", e);
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send message. Please try again or contact us at thecheatschoolcode@gmail.com"));
        }
    }

    @GetMapping("/failed")
    public ResponseEntity<ApiResponse<List<TCSContact>>> getFailedSubmissions() {
        log.info("Fetching failed contact submissions");
        List<TCSContact> failed = contactRepository.findByStatus("EMAIL_FAILED");
        return ResponseEntity.ok(ApiResponse.success(failed));
    }

    @PostMapping(consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<ApiResponse<String>> submitContactFormFallback(@RequestBody String body) {
        try {
            TCSContactRequest request = objectMapper.readValue(body, TCSContactRequest.class);
            return submitContactForm(request);
        } catch (Exception ex) {
            log.error("Failed to parse contact request body", ex);
            return ResponseEntity.status(400).body(ApiResponse.error("Invalid request body. Please send JSON with application/json Content-Type."));
        }
    }
}