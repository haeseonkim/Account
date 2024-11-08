package com.wev.domain.accounttimezone.service;

import com.wev.domain.accounttimezone.model.AccountTimezone;
import com.wev.domain.accounttimezone.repository.AccountTimezoneRepository;
import com.wev.domain.accounttimezone.service.AccountTimezoneService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.time.ZonedDateTime;

@ExtendWith(MockitoExtension.class)
class AccountTimezoneServiceTest {

    @Mock
    private AccountTimezoneRepository accountTimezoneRepository;

    @InjectMocks
    private AccountTimezoneService accountTimezoneService;

    @Nested
    class WhenTimezoneIsNull {

        @Test
        void thenUpdateIsRequired() {
            Long accountId = 1L;
            AccountTimezone accountTimezone = AccountTimezone.builder()
                    .timezone(null)
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountTimezoneRepository.findByAccountId(accountId)).thenReturn(Optional.of(accountTimezone));

            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isTrue();
        }
    }

    @Nested
    class WhenOffsetIsMoreThanTwoHours {

        @Test
        void thenUpdateIsRequired() {
            Long accountId = 2L;
            AccountTimezone accountTimezone = AccountTimezone.builder()
                    .timezone("America/New_York")
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountTimezoneRepository.findByAccountId(accountId)).thenReturn(Optional.of(accountTimezone));

            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isTrue();
        }
    }

    @Nested
    class WhenOffsetIsLessThanTwoHours {

        @Test
        void thenUpdateIsNotRequired() {
            Long accountId = 3L;
            AccountTimezone accountTimezone = AccountTimezone.builder()
                    .timezone("Asia/Seoul")
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountTimezoneRepository.findByAccountId(accountId)).thenReturn(Optional.of(accountTimezone));

            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, "Asia/Tokyo");

            assertThat(result).isFalse();
        }
    }

    @Nested
    class WhenAccountTimezoneNotFound {

        @Test
        void thenUpdateIsNotRequired() {
            Long accountId = 4L;
            when(accountTimezoneRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isFalse();
        }
    }
}

