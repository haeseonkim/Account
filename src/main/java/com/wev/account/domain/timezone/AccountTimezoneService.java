package com.wev.account.domain.timezone;

import com.wev.account.domain.timezone.exception.AccountNotFoundException;
import com.wev.account.domain.timezone.exception.TimezoneUpdateDeferredException;
import com.wev.account.domain.timezone.model.*;
import com.wev.account.domain.timezone.model.AccountTimezoneWebDTO.*;
import com.wev.account.domain.timezone.model.AccountTimezoneServiceDTO.*;
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

    public static final int MAX_OFFSET_DIFF_SECONDS = 60 * 60 * 2;
    private final AccountTimezoneRepository accountTimezoneRepository;
    private final AccountTimezoneMapper accountTimezoneMapper;

    // 1) timezone 업데이트 여부 확인
    public boolean isTimezoneUpdateRequired(Long accountId, String currentTimezone) {
        AccountTimezoneDTO accountTimezoneDTO = findAccountDTOOrThrow(accountId);

        if (isTimezoneNull(accountTimezoneDTO)) {
            return true;
        }

        return isOffsetDifferenceAboveThreshold(accountTimezoneDTO.getTimezone(), currentTimezone);
    }

    // 2) timezone or deferred 업데이트
    @Transactional
    public GetWebRes updateTimezoneAndDeferred(UpdateDTO updateDTO) {
        AccountTimezone accountTimezone = findAccountOrThrow(updateDTO.getAccountId());

        if (isUpdateDeferred(accountTimezone)) {
            throw new TimezoneUpdateDeferredException(accountTimezone.getUpdateDeferredUntil());
        }

        return updateAccountTimezone(accountTimezone, updateDTO);
    }

    // 3) 모든 timezone 조회 (zoneId 사용 - IANA 표준 타임존 데이터베이스)
    public List<String> getAllTimezones() {
        return ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    private AccountTimezoneDTO findAccountDTOOrThrow(Long accountId) {
        return accountTimezoneRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private AccountTimezone findAccountOrThrow(Long accountId) {
        return accountTimezoneRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private GetWebRes updateAccountTimezone(AccountTimezone accountTimezone, UpdateDTO updateDTO) {
        if (updateDTO.getTimezone() != null) {
            accountTimezone.updateTimezone(updateDTO.getTimezone());
        }

        if (updateDTO.isDeferred()) {
            accountTimezone.setUpdateDeferredUntil1Month();
        }

        return accountTimezoneMapper.toWebDto(accountTimezoneRepository.save(accountTimezone));
    }

    private boolean isTimezoneNull(AccountTimezoneDTO accountTimezoneDTO) {
        return accountTimezoneDTO.getTimezone() == null;
    }

    private boolean isOffsetDifferenceAboveThreshold(String dbTimezone, String currentTimezone) {
        ZoneId dbZoneId = ZoneId.of(dbTimezone);
        ZoneId currentZoneId = ZoneId.of(currentTimezone);

        ZonedDateTime dbZoneTime = ZonedDateTime.now(dbZoneId);
        ZonedDateTime currentZoneTime = ZonedDateTime.now(currentZoneId);

        int offsetDifference = Math.abs(dbZoneTime.getOffset().getTotalSeconds() - currentZoneTime.getOffset().getTotalSeconds());
        return offsetDifference >= MAX_OFFSET_DIFF_SECONDS;
    }

    private boolean isUpdateDeferred(AccountTimezone accountTimezone) {
        return accountTimezone.getUpdateDeferredUntil() != null
                && accountTimezone.getUpdateDeferredUntil().isAfter(ZonedDateTime.now());
    }
}