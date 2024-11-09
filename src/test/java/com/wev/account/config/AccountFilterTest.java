package com.wev.account.config;

import com.wev.account.context.RequestContext;
import com.wev.account.exception.MissingHeaderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountFilterTest {

    @Mock
    private RequestContext requestContext;

    @InjectMocks
    private AccountFilter accountFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    @DisplayName("헤더가 올바르게 있으면 => timezone, accountId 설정")
    void testFilterWithValidHeaders() throws ServletException, IOException {
        // given
        String timeZone = "America/New_York";
        String accountId = "999";
        request.addHeader("CloudFront-Viewer-Time-Zone", timeZone);
        request.addHeader("Account-Id", accountId);

        // when
        accountFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(requestContext).setTimezone(timeZone); // 타임존 설정 확인
        verify(requestContext).setAccountId(Long.valueOf(accountId)); // accountId 설정 확인
    }

    @Test
    @DisplayName("CloudFront-Viewer-Time-Zone 헤더가 없으면 => throw MissingHeaderException()")
    void testFilterWithoutTimezoneHeader() {
        // given
        request.addHeader("Account-Id", "999");

        // then
        assertThrows(MissingHeaderException.class, () ->
                accountFilter.doFilterInternal(request, response, filterChain)
        );
    }

    @Test
    @DisplayName("Account-Id 헤더가 없으면 => timezone만 설정")
    void testFilterWithoutAccountIdHeader() throws ServletException, IOException {
        // given
        String timeZone = "America/New_York";
        request.addHeader("CloudFront-Viewer-Time-Zone", timeZone);

        // when
        accountFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(requestContext).setTimezone(timeZone); // 타임존 설정 확인
        verify(requestContext, never()).setAccountId(anyLong()); // accountId는 설정되지 않음 확인
    }
}
