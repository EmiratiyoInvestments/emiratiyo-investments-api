package com.emiratiyo.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class EmiraInternalInterceptorTest {

    private EmiraInternalInterceptor interceptor;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        Bucket bucket = Bucket4j.builder()
                .addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofMinutes(1))))
                .build();

        interceptor = new EmiraInternalInterceptor(bucket, new ObjectMapper());
        ReflectionTestUtils.setField(interceptor, "internalSecret", "secret-key");
        response = new MockHttpServletResponse();
    }

    @Test
    void allowsNonInternalPathsWithoutAuth() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/contact");

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsMissingInternalKeyForHistoryEndpoint() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/internal/emira/history");

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void rejectsInvalidInternalKeyForAnalyseEndpointWithJsonBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/internal/emira/analyse");
        request.addHeader("X-Internal-Key", "wrong-key");

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("\"status\":\"error\"");
        assertThat(response.getContentAsString()).contains("Unauthorized");
    }

    @Test
    void allowsAuthorizedAnalyseRequestAndEnforcesRateLimit() throws Exception {
        MockHttpServletRequest firstRequest = authorizedAnalyseRequest();
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();

        boolean firstAllowed = interceptor.preHandle(firstRequest, firstResponse, new Object());
        assertThat(firstAllowed).isTrue();

        MockHttpServletRequest secondRequest = authorizedAnalyseRequest();
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();

        boolean secondAllowed = interceptor.preHandle(secondRequest, secondResponse, new Object());

        assertThat(secondAllowed).isFalse();
        assertThat(secondResponse.getStatus()).isEqualTo(429);
        assertThat(secondResponse.getContentAsString()).contains("Rate limit exceeded");
    }

    private MockHttpServletRequest authorizedAnalyseRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/internal/emira/analyse");
        request.addHeader("X-Internal-Key", "secret-key");
        return request;
    }
}
