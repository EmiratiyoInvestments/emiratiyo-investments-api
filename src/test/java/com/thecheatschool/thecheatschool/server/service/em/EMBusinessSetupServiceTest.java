package com.thecheatschool.thecheatschool.server.service.em;

import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupRequest;
import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupSubmission;
import com.thecheatschool.thecheatschool.server.repository.EMBusinessSetupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EMBusinessSetupServiceTest {

    @Mock
    private EMEmailService emailService;

    @Mock
    private EMBusinessSetupRepository businessSetupRepository;

    private EMBusinessSetupService service;

    @BeforeEach
    void setUp() {
        service = new EMBusinessSetupService(emailService, businessSetupRepository);
    }

    private EMBusinessSetupRequest createValidRequest() {
        EMBusinessSetupRequest request = new EMBusinessSetupRequest();
        request.setFullName("Talha Ahmed");
        request.setEmail("talha@example.com");
        request.setMobileNumber("+971 50 123 4567");
        request.setCountryOfResidence("United Arab Emirates");
        return request;
    }

    @Test
    void testProcessBusinessSetupSuccess() {
        EMBusinessSetupRequest request = createValidRequest();
        doNothing().when(emailService).sendBusinessSetupEmail(request);

        when(businessSetupRepository.save(any(EMBusinessSetupSubmission.class)))
                .thenAnswer(invocation -> {
                    EMBusinessSetupSubmission submission = invocation.getArgument(0);
                    if (submission.getId() == null) {
                        submission.setId(1L);
                    }
                    return submission;
                });

        assertDoesNotThrow(() -> service.processBusinessSetup(request));

        verify(emailService, times(1)).sendBusinessSetupEmail(request);
        verify(businessSetupRepository, atLeastOnce()).save(any(EMBusinessSetupSubmission.class));
    }

    @Test
    void testProcessBusinessSetupEmailFailure_SavesCorrectData() {
        EMBusinessSetupRequest request = createValidRequest();
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailService).sendBusinessSetupEmail(request);

        ArgumentCaptor<EMBusinessSetupSubmission> captor = ArgumentCaptor.forClass(EMBusinessSetupSubmission.class);

        when(businessSetupRepository.save(any(EMBusinessSetupSubmission.class)))
                .thenAnswer(invocation -> {
                    EMBusinessSetupSubmission submission = invocation.getArgument(0);
                    if (submission.getId() == null) {
                        submission.setId(1L);
                    }
                    return submission;
                });

        LocalDateTime beforeTest = LocalDateTime.now();

        assertDoesNotThrow(() -> service.processBusinessSetup(request));

        verify(businessSetupRepository, atLeast(2)).save(captor.capture());
        EMBusinessSetupSubmission saved = captor.getAllValues().get(captor.getAllValues().size() - 1);

        assertEquals("Talha Ahmed", saved.getFullName());
        assertEquals("talha@example.com", saved.getEmail());
        assertEquals("+971 50 123 4567", saved.getMobileNumber());
        assertEquals("United Arab Emirates", saved.getCountryOfResidence());
        assertEquals("EMAIL_FAILED", saved.getStatus());
        assertNotNull(saved.getSubmittedAt());
        assertNotNull(saved.getExpiresAt());

        assertTrue(saved.getExpiresAt().isAfter(beforeTest.plusDays(29)));
        assertTrue(saved.getExpiresAt().isBefore(beforeTest.plusDays(31)));
    }
}
