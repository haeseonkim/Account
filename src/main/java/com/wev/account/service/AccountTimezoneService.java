package com.wev.account.service;

import com.wev.account.repository.AccountRepository;
import com.wev.account.exception.AccountNotFoundException;
import com.wev.account.model.Account;
import com.wev.account.model.AccountTimezoneMapper;
import com.wev.account.model.AccountTimezoneWebDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountTimezoneService {
    private final int MAX_OFFSET_DIFF_SECONDS = 60 * 60 * 2;

    private final AccountRepository accountRepository;

    // 1. timezone 업데이트 여부 확인
    public boolean isTimezoneUpdateRequired(Long accountId, String currentTimezone) {
        Account account = findAccountOrThrow(accountId);

        if (!isUpdateDeferredNull(account) && !isAfterUpdateDeferred(account)) {
            return false;
        }

        if (isTimezoneNull(account)) {
            return true;
        }

        return isOffsetDifferenceAboveThreshold(account.getTimezone(), currentTimezone);
    }

    // 2. timezone or deferred 업데이트
    // 1) account가 없으면 에러를 리턴
    @Transactional
    public GetWebRes updateTimezoneAndDeferred(Long accountId, String currentTimezone, boolean isDeferred) {
        Account account = findAccountOrThrow(accountId);

        return updateAccountTimezone(account, currentTimezone, isDeferred);
    }

    // 3. 모든 timezone 조회 (zoneId 사용 - IANA 표준 타임존 데이터베이스)
    public List<String> getAllTimezones() {
        return ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    private Account findAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private GetWebRes updateAccountTimezone(Account account, String timezone, boolean isDeferred) {
        if (timezone != null) {
            account.updateTimezone(timezone);
        }

        if (isDeferred) {
            account.setUpdateDeferredUntil1Month();
        }

        return AccountTimezoneMapper.INSTANCE.toWebDto(accountRepository.save(account));
    }

    private boolean isTimezoneNull(Account account) {
        return account.getTimezone() == null;
    }

    private boolean isUpdateDeferredNull(Account account) {
        return account.getUpdateDeferredUntil() == null;
    }

    private boolean isOffsetDifferenceAboveThreshold(String savedTimezone, String currentTimezone) {
        ZoneId savedZoneId = ZoneId.of(savedTimezone);
        ZoneId currentZoneId = ZoneId.of(currentTimezone);

        ZonedDateTime savedZoneTime = ZonedDateTime.now(savedZoneId);
        ZonedDateTime currentZoneTime = ZonedDateTime.now(currentZoneId);

        int offsetDifference = Math.abs(savedZoneTime.getOffset().getTotalSeconds() - currentZoneTime.getOffset().getTotalSeconds());
        return offsetDifference >= MAX_OFFSET_DIFF_SECONDS;
    }

    private boolean isAfterUpdateDeferred(Account account) {
        return ZonedDateTime.now().isAfter(account.getUpdateDeferredUntil());
    }
}