package com.emiratiyo.api.service.email;

import com.emiratiyo.api.dto.BusinessSetupRequest;
import com.emiratiyo.api.dto.ContactRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateTest {

    private final ContactEmailTemplate contactEmailTemplate = new ContactEmailTemplate();
    private final BusinessSetupEmailTemplate businessSetupEmailTemplate = new BusinessSetupEmailTemplate();

    @Test
    void contactTemplateBuildsExpectedSections() {
        ContactRequest request = ContactRequest.builder()
                .name("Talha <Ahmed>")
                .phone("+971500000000")
                .email("talha@example.com")
                .message("Hello\nWorld")
                .build();

        String html = contactEmailTemplate.build(request);

        assertThat(html).contains("NEW CONTACT MESSAGE");
        assertThat(html).contains("Name");
        assertThat(html).contains("Phone");
        assertThat(html).contains("Email");
        assertThat(html).contains("Message");
        assertThat(html).contains("Talha &lt;Ahmed&gt;");
        assertThat(html).contains("Hello");
        assertThat(html).contains("World");
    }

    @Test
    void businessSetupTemplateBuildsExpectedSections() {
        BusinessSetupRequest request = BusinessSetupRequest.builder()
                .fullName("Talha Ahmed")
                .email("talha@example.com")
                .mobileNumber("+971500000000")
                .countryOfResidence("UAE")
                .build();

        String html = businessSetupEmailTemplate.build(request);

        assertThat(html).contains("BUSINESS SETUP REQUEST");
        assertThat(html).contains("Full Name");
        assertThat(html).contains("Mobile Number");
        assertThat(html).contains("Country of Residence");
        assertThat(html).contains("Talha Ahmed");
        assertThat(html).contains("talha@example.com");
        assertThat(html).contains("UAE");
    }
}
