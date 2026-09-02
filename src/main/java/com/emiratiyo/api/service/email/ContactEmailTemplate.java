package com.emiratiyo.api.service.email;

import com.emiratiyo.api.dto.ContactRequest;
import org.springframework.stereotype.Component;

@Component
public class ContactEmailTemplate {

    public String build(ContactRequest request) {
        StringBuilder html = new StringBuilder();
        EmailHtmlSupport.appendDocumentStart(html);
        EmailHtmlSupport.appendHeader(html, "NEW CONTACT MESSAGE");
        EmailHtmlSupport.appendContentStart(html);
        EmailHtmlSupport.addRow(html, "Name", request.name());
        EmailHtmlSupport.addRow(html, "Phone", request.phone());
        EmailHtmlSupport.addRow(html, "Email", request.email());

        String message = request.message();
        if (message == null || message.trim().isEmpty()) {
            message = "(No message provided)";
        }
        EmailHtmlSupport.addRow(
                html,
                "Message",
                EmailHtmlSupport.escapeHtml(message).replace("\n", "<br/>"));

        EmailHtmlSupport.appendContentEnd(html);
        EmailHtmlSupport.appendFooter(html);
        EmailHtmlSupport.appendDocumentEnd(html);
        return html.toString();
    }
}
