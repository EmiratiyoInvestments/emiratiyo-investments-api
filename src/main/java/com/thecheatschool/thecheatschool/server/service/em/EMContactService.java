package com.thecheatschool.thecheatschool.server.service.em;

import com.thecheatschool.thecheatschool.server.model.em.EMContact;
import com.thecheatschool.thecheatschool.server.model.em.EMContactRequest;
import com.thecheatschool.thecheatschool.server.repository.em.EMContactRepository;
import com.thecheatschool.thecheatschool.server.util.InputSanitizer;
import com.thecheatschool.thecheatschool.server.util.RequestIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EMContactService {

    private final EMEmailService emailService;
    private final EMContactRepository contactRepository;

    public void processContactForm(EMContactRequest request) {
        String requestId = RequestIdUtil.generate("EM-REQ-");

        log.info("[{}] Processing EM contact form submission", requestId);

        EMContact submission = buildSubmission(request);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setExpiresAt(LocalDateTime.now().plusDays(30));
        contactRepository.save(submission);

        emailService.sendContactEmail(request); // async — returns immediately
        log.info("[{}] EM contact form saved, email dispatched asynchronously", requestId);
    }

    private EMContact buildSubmission(EMContactRequest request) {
        EMContact submission = new EMContact();
        submission.setName(InputSanitizer.sanitize(request.getName()));
        submission.setPhone(InputSanitizer.sanitize(request.getPhone()));
        submission.setEmail(request.getEmail());
        submission.setMessage(InputSanitizer.sanitize(request.getMessage()));
        return submission;
    }
}