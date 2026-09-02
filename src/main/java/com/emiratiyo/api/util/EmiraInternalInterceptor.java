package com.emiratiyo.api.util;

import com.emiratiyo.api.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class EmiraInternalInterceptor implements HandlerInterceptor {

    private static final String INTERNAL_EMIRA_PREFIX = "/api/v1/internal/emira";
    private static final String ANALYSE_PATH = "/api/v1/internal/emira/analyse";

    @Value("${emira.internal.secret:}")
    private String internalSecret;

    @Qualifier("emiraBucket")
    private final Bucket emiraBucket;

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!request.getRequestURI().startsWith(INTERNAL_EMIRA_PREFIX)) {
            return true;
        }

        String internalKey = request.getHeader("X-Internal-Key");
        if (!EmiraAuthUtil.isAuthorized(internalKey, internalSecret)) {
            writeResponse(response, request.getRequestURI(), HttpStatus.FORBIDDEN, "Unauthorized");
            return false;
        }

        if (ANALYSE_PATH.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            if (!emiraBucket.tryConsume(1)) {
                writeResponse(response, request.getRequestURI(), HttpStatus.TOO_MANY_REQUESTS,
                        "Rate limit exceeded. Please try again later.");
                return false;
            }
        }

        return true;
    }

    private void writeResponse(HttpServletResponse response, String uri, HttpStatus status, String message)
            throws Exception {
        response.setStatus(status.value());

        if (ANALYSE_PATH.equals(uri)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(message)));
        }
    }
}
