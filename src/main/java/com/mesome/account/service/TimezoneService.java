package com.mesome.account.service;

import com.mesome.account.entity.Account;
import com.mesome.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TimezoneService {
    private final AccountRepository accountRepository;


    public boolean isTimezoneUpdateRequired(Long accountId, String currentTimezone) {
        Optional<Account> accountOpt = accountRepository.findByAccountId(accountId);

        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();

            // 타임존이 없는 경우 업데이트 필요
            if (account.getTimezone() == null) {
                return true;
            }

            // 오프셋 차이가 2시간 이상일 경우
            ZoneId dbZoneId = ZoneId.of(account.getTimezone());
            ZoneId currentZoneId = ZoneId.of(currentTimezone);

            // 현재 시간 ZonedDateTime 객체 생성
            ZonedDateTime dbZoneTime = ZonedDateTime.now(dbZoneId);
            ZonedDateTime currentZoneTime = ZonedDateTime.now(currentZoneId);

            // 두 타임존 오프셋 차이 계산 (두시간 차 7200초)
            return Math.abs(dbZoneTime.getOffset().getTotalSeconds() - currentZoneTime.getOffset().getTotalSeconds()) >= 7200;
        }

        // 계정을 찾지 못한 경우 업데이트가 필요하지 않다고 가정
        return false;
    }
}
