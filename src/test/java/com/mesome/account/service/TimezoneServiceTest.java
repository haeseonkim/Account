package com.mesome.account.service;

import com.mesome.account.entity.Account;
import com.mesome.account.repository.AccountRepository;
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
class TimezoneServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TimezoneService timezoneService;

    @Nested
    class WhenTimezoneIsNull {

        @Test
        void thenUpdateIsRequired() {
            Long accountId = 1L;
            Account account = Account.builder()
                    .timezone(null)
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));

            boolean result = timezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isTrue();
        }
    }

    @Nested
    class WhenOffsetIsMoreThanTwoHours {

        @Test
        void thenUpdateIsRequired() {
            Long accountId = 2L;
            Account account = Account.builder()
                    .timezone("America/New_York")
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));

            boolean result = timezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isTrue();
        }
    }

    @Nested
    class WhenOffsetIsLessThanTwoHours {

        @Test
        void thenUpdateIsNotRequired() {
            Long accountId = 3L;
            Account account = Account.builder()
                    .timezone("Asia/Seoul")
                    .updateDeferredUntil(ZonedDateTime.now().minusMonths(1))
                    .build();

            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(account));

            boolean result = timezoneService.isTimezoneUpdateRequired(accountId, "Asia/Tokyo");

            assertThat(result).isFalse();
        }
    }

    @Nested
    class WhenAccountNotFound {

        @Test
        void thenUpdateIsNotRequired() {
            Long accountId = 4L;
            when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

            boolean result = timezoneService.isTimezoneUpdateRequired(accountId, "Asia/Seoul");

            assertThat(result).isFalse();
        }
    }
}

