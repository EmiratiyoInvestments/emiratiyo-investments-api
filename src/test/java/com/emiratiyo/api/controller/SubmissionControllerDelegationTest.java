package com.emiratiyo.api.controller;

import com.emiratiyo.api.dto.ApiResponse;
import com.emiratiyo.api.dto.BusinessSetupRequest;
import com.emiratiyo.api.dto.ContactRequest;
import com.emiratiyo.api.entity.BusinessSetupEntity;
import com.emiratiyo.api.entity.ContactEntity;
import com.emiratiyo.api.service.BusinessSetupService;
import com.emiratiyo.api.service.ContactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerDelegationTest {

    @Mock
    private ContactService contactService;

    @Mock
    private BusinessSetupService businessSetupService;

    @InjectMocks
    private ContactController contactController;

    @InjectMocks
    private BusinessSetupController businessSetupController;

    @Test
    void contactFailedEndpointDelegatesToService() {
        List<ContactEntity> failed = List.of(ContactEntity.builder().status("EMAIL_FAILED").build());
        when(contactService.getFailedSubmissions()).thenReturn(failed);

        ResponseEntity<ApiResponse<List<ContactEntity>>> response = contactController.getFailedSubmissions();

        assertThat(response.getBody()).isEqualTo(ApiResponse.success(failed));
        verify(contactService).getFailedSubmissions();
    }

    @Test
    void businessSetupFailedEndpointDelegatesToService() {
        List<BusinessSetupEntity> failed = List.of(BusinessSetupEntity.builder().status("EMAIL_FAILED").build());
        when(businessSetupService.getFailedSubmissions()).thenReturn(failed);

        ResponseEntity<ApiResponse<List<BusinessSetupEntity>>> response = businessSetupController.getFailedSubmissions();

        assertThat(response.getBody()).isEqualTo(ApiResponse.success(failed));
        verify(businessSetupService).getFailedSubmissions();
    }

    @Test
    void contactSubmitDelegatesToService() {
        ContactRequest request = ContactRequest.builder()
                .name("Talha")
                .phone("+971500000000")
                .email("talha@example.com")
                .message("Hello")
                .build();

        ResponseEntity<ApiResponse<String>> response = contactController.submitContactForm(request);

        assertThat(response.getBody()).isEqualTo(
                ApiResponse.success("Message sent successfully! We'll get back to you soon."));
        verify(contactService).processContactForm(request);
    }

    @Test
    void businessSetupSubmitDelegatesToService() {
        BusinessSetupRequest request = BusinessSetupRequest.builder()
                .fullName("Talha Ahmed")
                .email("talha@example.com")
                .mobileNumber("+971500000000")
                .countryOfResidence("UAE")
                .build();

        ResponseEntity<ApiResponse<String>> response = businessSetupController.submit(request);

        assertThat(response.getBody()).isEqualTo(
                ApiResponse.success("Your business setup request has been received. We will contact you shortly."));
        verify(businessSetupService).processBusinessSetup(request);
    }
}
