package com.emiratiyo.api.service.email;

import com.emiratiyo.api.dto.BusinessSetupRequest;
import org.springframework.stereotype.Component;

@Component
public class BusinessSetupEmailTemplate {

    public String build(BusinessSetupRequest request) {
        StringBuilder html = new StringBuilder();
        EmailHtmlSupport.appendDocumentStart(html);
        EmailHtmlSupport.appendHeader(html, "BUSINESS SETUP REQUEST");
        EmailHtmlSupport.appendContentStart(html);
        EmailHtmlSupport.addRow(html, "Full Name", request.fullName());
        EmailHtmlSupport.addRow(html, "Email", request.email());
        EmailHtmlSupport.addRow(html, "Mobile Number", request.mobileNumber());
        EmailHtmlSupport.addRow(html, "Country of Residence", request.countryOfResidence());
        EmailHtmlSupport.appendContentEnd(html);
        EmailHtmlSupport.appendFooter(html);
        EmailHtmlSupport.appendDocumentEnd(html);
        return html.toString();
    }
}
