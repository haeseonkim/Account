package com.wev.domain.accounttimezone.service;

import com.wev.domain.accounttimezone.model.AccountTimezone;
import com.wev.domain.accounttimezone.repository.AccountTimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AccountTimezoneService {
    private final AccountTimezoneRepository accountTimezoneRepository;

    public boolean isTimezoneUpdateRequired(Long accountId, String currentTimezone) {
        Optional<AccountTimezone> accountOpt = accountTimezoneRepository.findByAccountId(accountId);

        if (accountOpt.isPresent()) {
            AccountTimezone accountTimezone = accountOpt.get();

            // 타임존이 없는 경우 업데이트 필요
            if (isTimezoneNull(accountTimezone)) {
                return true;
            }

            // 오프셋 차이가 2시간 이상인지 확인
            return isOffsetDifferenceAboveThreshold(accountTimezone.getTimezone(), currentTimezone);
        }

        // 계정을 찾지 못한 경우 업데이트가 필요하지 않다고 가정
        return false;
    }

    private boolean isTimezoneNull(AccountTimezone accountTimezone) {
        return accountTimezone.getTimezone() == null;
    }

    private boolean isOffsetDifferenceAboveThreshold(String dbTimezone, String currentTimezone) {
        ZoneId dbZoneId = ZoneId.of(dbTimezone);
        ZoneId currentZoneId = ZoneId.of(currentTimezone);

        ZonedDateTime dbZoneTime = ZonedDateTime.now(dbZoneId);
        ZonedDateTime currentZoneTime = ZonedDateTime.now(currentZoneId);

        int offsetDifference = Math.abs(dbZoneTime.getOffset().getTotalSeconds() - currentZoneTime.getOffset().getTotalSeconds());
        return offsetDifference >= 7200;
    }
}
