package com.emiratiyo.api.service;

import com.emiratiyo.api.dto.BusinessSetupRequest;
import com.emiratiyo.api.dto.ContactRequest;

public interface EmailSender {

    void sendBusinessSetupEmail(BusinessSetupRequest request);

    void sendContactEmail(ContactRequest request);
}
