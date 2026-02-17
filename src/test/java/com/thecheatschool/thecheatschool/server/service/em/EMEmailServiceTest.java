package com.thecheatschool.thecheatschool.server.service.em;

import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupRequest;
import com.thecheatschool.thecheatschool.server.model.em.EMContactRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EMEmailServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private EMEmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EMEmailService();
        ReflectionTestUtils.setField(emailService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(emailService, "resendApiKey", "test-api-key");
        ReflectionTestUtils.setField(emailService, "recipientEmail", "admin@emiratiyo.com");
        ReflectionTestUtils.setField(emailService, "fromEmail", "onboarding@resend.dev");
    }

    private EMContactRequest createValidRequest() {
        EMContactRequest request = new EMContactRequest();
        request.setName("Talha Ahmed");
        request.setPhone("+971501234567");
        request.setEmail("talha@example.com");
        request.setMessage("Hello");
        return request;
    }

    private EMBusinessSetupRequest createValidBusinessSetupRequest() {
        EMBusinessSetupRequest request = new EMBusinessSetupRequest();
        request.setFullName("Talha Ahmed");
        request.setEmail("talha@example.com");
        request.setMobileNumber("+971 50 123 4567");
        request.setCountryOfResidence("United Arab Emirates");
        return request;
    }

    @Test
    void testSendContactEmailSuccess() {
        EMContactRequest request = createValidRequest();

        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"id\":\"email-123\"}", HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        assertDoesNotThrow(() -> emailService.sendContactEmail(request));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testSendContactEmailFailure_Non2xxResponse() {
        EMContactRequest request = createValidRequest();

        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"error\":\"Unauthorized\"}", HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendContactEmail(request));
        assertTrue(exception.getMessage().contains("Failed to send email"));
    }

    @Test
    void testSendContactEmailFailure_Exception() {
        EMContactRequest request = createValidRequest();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection timeout"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendContactEmail(request));
        assertTrue(exception.getMessage().contains("Failed to send email"));
    }

    @Test
    void testSendBusinessSetupEmailSuccess() {
        EMBusinessSetupRequest request = createValidBusinessSetupRequest();

        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"id\":\"email-123\"}", HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        assertDoesNotThrow(() -> emailService.sendBusinessSetupEmail(request));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testSendBusinessSetupEmailFailure_Non2xxResponse() {
        EMBusinessSetupRequest request = createValidBusinessSetupRequest();

        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"error\":\"Unauthorized\"}", HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendBusinessSetupEmail(request));
        assertTrue(exception.getMessage().contains("Failed to send email"));
    }

    @Test
    void testSendBusinessSetupEmailFailure_Exception() {
        EMBusinessSetupRequest request = createValidBusinessSetupRequest();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection timeout"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendBusinessSetupEmail(request));
        assertTrue(exception.getMessage().contains("Failed to send email"));
    }
}
