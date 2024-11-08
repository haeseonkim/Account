package com.wev.domain.accounttimezone.controller;

import com.wev.domain.accounttimezone.service.AccountTimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/timezone")
public class AccountTimezoneController {
    private final AccountTimezoneService accountTimezoneService;

    // 1. 타임존 업데이트 필요 여부 확인
    @GetMapping("/check")
    public boolean checkTimezoneUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "CloudFront-Viewer-Time-Zone", required = false) String currentTimezone
    ) {
        Long accountId = Long.valueOf(userDetails.getUsername());

        // 타임존 정보 없으면 변경할 필요 없음 리턴
        if (currentTimezone == null || currentTimezone.isEmpty()) {
            return false;
        }

        return accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone);
    }

    // 2. 타임존 업데이트
    // 3. 전체 타임존 목록 조회
}
