package com.luisgosampaio.adventura.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitInterceptorTest {

    private RateLimitInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitInterceptor();
    }

    @Test
    void allowsRequest_WhenTokensAvailable() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertThat(result).isTrue();
    }

    @Test
    void setsRemainingTokensHeader_OnFirstRequest() throws Exception {
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(request, response, new Object());

        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isEqualTo("19");
    }

    @Test
    void remainingTokensDecrement_AcrossRequests() throws Exception {
        when(request.getRemoteAddr()).thenReturn("172.16.0.1");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        MockHttpServletResponse thirdResponse = new MockHttpServletResponse();
        interceptor.preHandle(request, thirdResponse, new Object());

        assertThat(thirdResponse.getHeader("X-Rate-Limit-Remaining")).isEqualTo("17");
    }

    @Test
    void blocks21stRequest_FromSameIp() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("X-Rate-Limit-Retry-After-Seconds")).isNotNull();
    }

    @Test
    void differentIps_HaveIndependentBuckets() throws Exception {
        HttpServletRequest ip1 = mock(HttpServletRequest.class);
        HttpServletRequest ip2 = mock(HttpServletRequest.class);
        when(ip1.getRemoteAddr()).thenReturn("10.0.0.1");
        when(ip2.getRemoteAddr()).thenReturn("10.0.0.2");

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(ip1, new MockHttpServletResponse(), new Object());
        }

        boolean ip1Result = interceptor.preHandle(ip1, new MockHttpServletResponse(), new Object());
        boolean ip2Result = interceptor.preHandle(ip2, new MockHttpServletResponse(), new Object());

        assertThat(ip1Result).isFalse();
        assertThat(ip2Result).isTrue();
    }

    @Test
    void blockedRequest_SetsRetryAfterHeader() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.3");

        for (int i = 0; i < 20; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        interceptor.preHandle(request, response, new Object());

        String retryAfter = response.getHeader("X-Rate-Limit-Retry-After-Seconds");
        assertThat(retryAfter).isNotNull();
        assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);
    }
}