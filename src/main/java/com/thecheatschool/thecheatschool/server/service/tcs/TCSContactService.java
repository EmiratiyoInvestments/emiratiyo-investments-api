package com.thecheatschool.thecheatschool.server.service.tcs;

import com.thecheatschool.thecheatschool.server.model.tcs.TCSContact;
import com.thecheatschool.thecheatschool.server.model.tcs.TCSContactRequest;
import com.thecheatschool.thecheatschool.server.repository.tcs.TCSContactRepository;
import com.thecheatschool.thecheatschool.server.util.InputSanitizer;
import com.thecheatschool.thecheatschool.server.util.RequestIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TCSContactService {

    private final TCSEmailService emailService;
    private final TCSContactRepository contactRepository;

    public void processContactForm(TCSContactRequest request) {
        String requestId = RequestIdUtil.generate("REQ-");

        log.info("[{}] Processing contact form submission from college: {}, branch: {}",
                requestId, request.getCollege(), request.getBranch());

        TCSContact submission = buildSubmission(request);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setExpiresAt(LocalDateTime.now().plusDays(30));
        contactRepository.save(submission);

        emailService.sendContactEmail(request); // async — returns immediately
        log.info("[{}] Contact form saved, email dispatched asynchronously", requestId);
    }

    private TCSContact buildSubmission(TCSContactRequest request) {
        TCSContact submission = new TCSContact();
        submission.setFullName(InputSanitizer.sanitize(request.getFullName()));
        submission.setEmail(request.getEmail());
        submission.setPhoneNumber(InputSanitizer.sanitize(request.getPhoneNumber()));
        submission.setCollege(InputSanitizer.sanitize(request.getCollege()));
        submission.setYearOfStudy(InputSanitizer.sanitize(request.getYearOfStudy()));
        submission.setBranch(InputSanitizer.sanitize(request.getBranch()));
        submission.setHearAboutUs(InputSanitizer.sanitize(request.getHearAboutUs()));
        submission.setHearAboutUsOther(InputSanitizer.sanitize(request.getHearAboutUsOther()));
        return submission;
    }
}