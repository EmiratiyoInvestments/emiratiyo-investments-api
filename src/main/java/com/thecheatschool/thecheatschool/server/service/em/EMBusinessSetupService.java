package com.thecheatschool.thecheatschool.server.service.em;

import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupRequest;
import com.thecheatschool.thecheatschool.server.model.em.EMBusinessSetupSubmission;
import com.thecheatschool.thecheatschool.server.repository.em.EMBusinessSetupRepository;
import com.thecheatschool.thecheatschool.server.util.InputSanitizer;
import com.thecheatschool.thecheatschool.server.util.RequestIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EMBusinessSetupService {

    private final EMEmailService emailService;
    private final EMBusinessSetupRepository businessSetupRepository;

    public void processBusinessSetup(EMBusinessSetupRequest request) {
        String requestId = RequestIdUtil.generate("EM-BIZ-REQ-");

        log.info("[{}] Processing EM business setup submission", requestId);

        EMBusinessSetupSubmission submission = buildSubmission(request);
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setExpiresAt(LocalDateTime.now().plusDays(30));
        businessSetupRepository.save(submission);

        emailService.sendBusinessSetupEmail(request); // async — returns immediately
        log.info("[{}] EM business setup saved, email dispatched asynchronously", requestId);
    }

    private EMBusinessSetupSubmission buildSubmission(EMBusinessSetupRequest request) {
        EMBusinessSetupSubmission submission = new EMBusinessSetupSubmission();
        submission.setFullName(InputSanitizer.sanitize(request.getFullName()));
        submission.setEmail(request.getEmail());
        submission.setMobileNumber(InputSanitizer.sanitize(request.getMobileNumber()));
        submission.setCountryOfResidence(InputSanitizer.sanitize(request.getCountryOfResidence()));
        return submission;
    }
}