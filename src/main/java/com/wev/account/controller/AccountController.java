package com.wev.account.controller;

import com.wev.account.context.RequestContext;
import com.wev.account.service.AccountTimezoneService;
import com.wev.account.model.AccountTimezoneWebDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountTimezoneService accountTimezoneService;
    private final ObjectProvider<RequestContext> requestContextObjectProvider;

    // 1) 타임존 업데이트 필요 여부 확인
    @GetMapping("/timezone/check")
    public ResponseEntity<Boolean> checkTimezoneUpdate() {
        RequestContext requestContext = requestContextObjectProvider.getObject();
        return ResponseEntity.ok(accountTimezoneService.isTimezoneUpdateRequired(requestContext.getAccountId(), requestContext.getTimezone()));
    }

    // 2) 타임존 업데이트 - 사용자가 선택한 타임존으로 세팅될 수 있음. 유보시간 설정 할 수도 있음.
    @PutMapping("/timezone/update")
    public ResponseEntity<GetWebRes> updateTimezone(
            @RequestParam(required = false, defaultValue = "false") boolean updateTimezone,
            @RequestParam(required = false) String timezone,
            @RequestParam(required = false, defaultValue = "false") boolean isDeferred
    ) {
        RequestContext requestContext = requestContextObjectProvider.getObject();

        // 타임존 업데이트 여부에 따라 timezone 값을 설정
        String effectiveTimezone = null;
        if (updateTimezone) {
            effectiveTimezone = (timezone != null) ? timezone : requestContext.getTimezone();
        }

        return ResponseEntity.ok(accountTimezoneService.updateTimezoneAndDeferred(requestContext.getAccountId(), effectiveTimezone, isDeferred));
    }

    // 3) 전체 타임존 목록 조회
    @GetMapping("/timezone/all")
    public ResponseEntity<List<String>> findAllTimezones() {
        return ResponseEntity.ok(accountTimezoneService.getAllTimezones());
    }
}
