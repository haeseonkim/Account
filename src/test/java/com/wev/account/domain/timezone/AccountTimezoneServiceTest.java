package com.wev.account.domain.timezone;

import com.wev.account.domain.timezone.exception.AccountNotFoundException;
import com.wev.account.domain.timezone.exception.TimezoneUpdateDeferredException;
import com.wev.account.domain.timezone.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountTimezoneServiceTest {

    @Mock
    private AccountTimezoneRepository accountTimezoneRepository;

    @Mock
    private AccountTimezoneMapper accountTimezoneMapper;

    @InjectMocks
    private AccountTimezoneService accountTimezoneService;

    private static final Long ACCOUNT_ID = 1L;
    private static final String VALID_TIMEZONE = "America/New_York";
    private static final String CURRENT_TIMEZONE = "Asia/Seoul";

    @BeforeEach
    void setUp() {
        // 초기화 설정이 필요하면 이곳에서 설정
    }

    @Test
    void testIsTimezoneUpdateRequiredWithNullTimezone() {
        // given
        AccountTimezoneDTO accountTimezoneDTO = mock(AccountTimezoneDTO.class);
        when(accountTimezoneDTO.getTimezone()).thenReturn(null);
        when(accountTimezoneRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(accountTimezoneDTO));

        // when
        boolean result = accountTimezoneService.isTimezoneUpdateRequired(ACCOUNT_ID, CURRENT_TIMEZONE);

        // then
        assertTrue(result);
        verify(accountTimezoneRepository).findByAccountId(ACCOUNT_ID);
    }

    @Test
    void testIsTimezoneUpdateRequiredWithDifferentOffset() {
        // given
        AccountTimezoneDTO accountTimezoneDTO = mock(AccountTimezoneDTO.class);
        when(accountTimezoneDTO.getTimezone()).thenReturn(VALID_TIMEZONE);
        when(accountTimezoneRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(accountTimezoneDTO));

        // when
        boolean result = accountTimezoneService.isTimezoneUpdateRequired(ACCOUNT_ID, CURRENT_TIMEZONE);

        // then
        assertTrue(result);
        verify(accountTimezoneRepository).findByAccountId(ACCOUNT_ID);
    }

    @Test
    void testIsTimezoneUpdateRequiredWithSameOffset() {
        // given
        AccountTimezoneDTO accountTimezoneDTO = mock(AccountTimezoneDTO.class);
        when(accountTimezoneDTO.getTimezone()).thenReturn(CURRENT_TIMEZONE); // 동일한 시간대를 설정
        when(accountTimezoneRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(accountTimezoneDTO));

        // when
        boolean result = accountTimezoneService.isTimezoneUpdateRequired(ACCOUNT_ID, CURRENT_TIMEZONE);

        // then
        assertFalse(result);
        verify(accountTimezoneRepository).findByAccountId(ACCOUNT_ID);
    }

    @Test
    void testUpdateTimezoneAndDeferredWhenDeferred() {
        // given
        AccountTimezone accountTimezone = mock(AccountTimezone.class);
        ZonedDateTime deferredUntil = ZonedDateTime.now().plusDays(1);
        when(accountTimezone.getUpdateDeferredUntil()).thenReturn(deferredUntil);
        when(accountTimezoneRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(accountTimezone));

        // then
        assertThrows(TimezoneUpdateDeferredException.class, ()
                -> accountTimezoneService.updateTimezoneAndDeferred(
                        AccountTimezoneServiceDTO.UpdateDTO.builder()
                                .accountId(ACCOUNT_ID)
                                .timezone(VALID_TIMEZONE)
                                .isDeferred(true)
                                .build()));
        verify(accountTimezoneRepository).findById(ACCOUNT_ID);
    }

    @Test
    void testUpdateTimezoneAndDeferredWithValidUpdate() {
        // given
        AccountTimezone accountTimezone = mock(AccountTimezone.class);
        when(accountTimezoneRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(accountTimezone));
        AccountTimezoneServiceDTO.UpdateDTO updateDTO = AccountTimezoneServiceDTO.UpdateDTO.builder()
                .accountId(ACCOUNT_ID)
                .timezone(VALID_TIMEZONE)
                .isDeferred(true)
                .build();
        AccountTimezoneWebDTO.GetWebRes webRes = AccountTimezoneWebDTO.GetWebRes.builder()
                .accountId(ACCOUNT_ID)
                .timezone(VALID_TIMEZONE)
                .updateDeferredUntil(ZonedDateTime.now().plusDays(1))
                .build();
        when(accountTimezoneMapper.toWebDto(any(AccountTimezone.class))).thenReturn(webRes);

        // when
        AccountTimezoneWebDTO.GetWebRes result = accountTimezoneService.updateTimezoneAndDeferred(updateDTO);

        // then
        assertNotNull(result);
        assertEquals(VALID_TIMEZONE, result.getTimezone());
        verify(accountTimezoneRepository).findById(ACCOUNT_ID);
        verify(accountTimezoneMapper).toWebDto(accountTimezone);
    }

    @Test
    void testGetAllTimezones() {
        // when
        List<String> timezones = accountTimezoneService.getAllTimezones();

        // then
        assertNotNull(timezones);
        assertTrue(timezones.contains("America/New_York")); // 예시로 타임존이 포함되어 있는지 확인
    }

    @Test
    void testAccountNotFoundExceptionThrown() {
        // given
        when(accountTimezoneRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());

        // then
        assertThrows(AccountNotFoundException.class, () -> accountTimezoneService.isTimezoneUpdateRequired(ACCOUNT_ID, CURRENT_TIMEZONE));
    }
}
