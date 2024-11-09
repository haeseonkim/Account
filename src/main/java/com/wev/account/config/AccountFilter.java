package com.wev.account.config;

import com.wev.account.context.RequestContext;
import com.wev.account.exception.MissingHeaderException;
import com.wev.account.exception.TimezoneNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AccountFilter extends OncePerRequestFilter {

    private final RequestContext requestContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // RequestContext  timezone 세팅
        String timeZone = request.getHeader("CloudFront-Viewer-Time-Zone");
        if (timeZone == null || timeZone.isEmpty()) {
            throw new MissingHeaderException("CloudFront-Viewer-Time-Zone");
        }
        requestContext.setTimezone(timeZone);


        // RequestContext  accountId 세팅
        String accountIdHeader = request.getHeader("Account-Id");
        if (accountIdHeader != null) {
            requestContext.setAccountId(Long.valueOf(accountIdHeader));
        }

        filterChain.doFilter(request, response);
    }
}
