package com.emiratiyo.api.service;

import com.emiratiyo.api.dto.BusinessSetupRequest;
import com.emiratiyo.api.dto.ContactRequest;
import com.emiratiyo.api.service.email.BusinessSetupEmailTemplate;
import com.emiratiyo.api.service.email.ContactEmailTemplate;
import com.emiratiyo.api.util.EmailUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";

    @Value("${em.resend.api.key:}")
    private String resendApiKey;

    @Value("${em.contact.recipient.email:}")
    private String recipientEmail;

    @Value("${em.contact.from.email:onboarding@resend.dev}")
    private String fromEmail;

    private final WebClient webClient;
    private final ContactEmailTemplate contactEmailTemplate;
    private final BusinessSetupEmailTemplate businessSetupEmailTemplate;

    @Async("taskExecutor")
    @CircuitBreaker(name = "resendEmail", fallbackMethod = "fallbackEmail")
    public void sendBusinessSetupEmail(BusinessSetupRequest request) {
        String emailHash = maskEmail(request.email());

        log.info("Starting EM business setup email send process, reply-to: {}", emailHash);

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("from", fromEmail);
        emailData.put("to", new String[]{recipientEmail});
        emailData.put("reply_to", request.email());
        emailData.put("subject", "Emiratiyo Investments - Business Setup Request (" + request.fullName() + ")");
        emailData.put("html", businessSetupEmailTemplate.build(request));

        webClient.post()
                .uri(RESEND_EMAILS_URL)
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emailData)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("EM business setup email sent successfully, reply-to: {}", emailHash))
                .doOnError(e -> log.error("Error sending EM business setup email via Resend API, reply-to: {}", emailHash, e))
                .subscribe();
    }

    @Async("taskExecutor")
    @CircuitBreaker(name = "resendEmail", fallbackMethod = "fallbackEmail")
    public void sendContactEmail(ContactRequest request) {
        String emailHash = maskEmail(request.email());

        log.info("Starting EM email send process, reply-to: {}", emailHash);

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("from", fromEmail);
        emailData.put("to", new String[]{recipientEmail});
        emailData.put("reply_to", request.email());
        emailData.put("subject", "Emiratiyo Investments - New Contact Message (" + request.name() + ")");
        emailData.put("html", contactEmailTemplate.build(request));

        webClient.post()
                .uri(RESEND_EMAILS_URL)
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emailData)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("EM email sent successfully, reply-to: {}", emailHash))
                .doOnError(e -> log.error("Error sending EM email via Resend API, reply-to: {}", emailHash, e))
                .subscribe();
    }

    /**
     * Fallback for Resend Email Circuit Breaker
     */
    private void fallbackEmail(Object request, Throwable t) {
        log.error("Circuit breaker opened for Resend Email Service. Failing gracefully. Error: {}", t.getMessage());
        // In a real app, we might save to a 'failed_emails' table here for retry later
    }

    private String maskEmail(String email) {
        return EmailUtils.maskEmail(email);
    }
}
