package com.thecheatschool.thecheatschool.server.service.tcs;

import com.thecheatschool.thecheatschool.server.model.tcs.TCSNotifyMeRequest;
import com.thecheatschool.thecheatschool.server.model.tcs.TCSNotifyMeSignup;
import com.thecheatschool.thecheatschool.server.repository.tcs.TCSNotifyMeRepository;
import com.thecheatschool.thecheatschool.server.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TCSNotifyMeService {

    private final TCSNotifyMeRepository notifyMeRepository;
    private final TCSEmailService emailService;

    public void processNotifyMe(TCSNotifyMeRequest request) {
        String requestId = generateRequestId();

        log.info("[{}] Processing notify-me signup, email: {}", requestId, request.getEmail());

        TCSNotifyMeSignup signup = notifyMeRepository.findByEmail(request.getEmail())
                .orElseGet(TCSNotifyMeSignup::new);

        boolean isNew = (signup.getId() == null);
        signup.setName(InputSanitizer.sanitize(request.getName()));
        signup.setEmail(request.getEmail());
        signup.setPhoneNumber(InputSanitizer.sanitize(request.getPhoneNumber()));
        if (isNew) signup.setSubmittedAt(LocalDateTime.now());
        signup.setUpdatedAt(LocalDateTime.now());
        signup.setStatus("SUBMITTED");
        notifyMeRepository.save(signup);

        emailService.sendNotifyMeEmail(request); // async — returns immediately
        log.info("[{}] Notify-me saved, email dispatched asynchronously", requestId);
    }

    private String generateRequestId() {
        return "NOTIFY-REQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
