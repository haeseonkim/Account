package com.wev.account.service;

import com.wev.account.model.Account;
import com.wev.account.exception.AccountNotFoundException;
import com.wev.account.model.AccountTimezoneMapper;
import com.wev.account.model.AccountTimezoneWebDTO;
import com.wev.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
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
    private AccountRepository accountRepository;

    private final AccountTimezoneMapper accountTimezoneMapper = Mappers.getMapper(AccountTimezoneMapper.class);

    @InjectMocks
    private AccountTimezoneService accountTimezoneService;

    private final Long accountId = 999L;
    private final String currentTimezone = "Asia/Seoul";
    private final ZonedDateTime updateDeferredUntilPast = ZonedDateTime.parse("2024-11-01T00:00:00Z");
    private final ZonedDateTime updateDeferredUntilFuture = ZonedDateTime.parse("2024-12-20T00:00:00Z");

    @Test
    @DisplayName("mapstruct 매핑 테스트")
    void toAccountTimezoneMapperTest() {
        // given
        Account account = Account.builder()
                .accountId(accountId)
                .timezone(currentTimezone)
                .updateDeferredUntil(updateDeferredUntilFuture)
                .build();

        // when
        AccountTimezoneWebDTO.GetWebRes webRes = accountTimezoneMapper.toWebDto(account);

        // then
        assertNotNull(webRes);  // 매핑 결과가 null이 아닌지 확인
        assertEquals(account.getAccountId(), webRes.getAccountId());  // ID 필드 매핑 검증
        assertEquals(account.getTimezone(), webRes.getTimezone());    // 타임존 필드 매핑 검증
        assertEquals(account.getUpdateDeferredUntil(), webRes.getUpdateDeferredUntil()); // 유보 시간 필드 매핑 검증

    }

    @Nested
    @DisplayName("isTimezoneUpdateRequiredTest")
    class isTimezoneUpdateRequiredTest {

        @Test
        @DisplayName("계정이 없을때 => throw AccountNotFoundException()")
        void NotExistAccount() {
            // given
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            // then
            assertThrows(AccountNotFoundException.class, () ->
                    accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone));
        }

        @Test
        @DisplayName("업데이트 유보시간이 null이 아니고 현재 유보시간이 지나지 않았다면 => return false")
        void UpdateDeferredUntilIsNullOrPast() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("America/New_York")
                    .updateDeferredUntil(updateDeferredUntilFuture)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone);

            // then
            assertFalse(result);
        }

        @Test
        @DisplayName("타임존 컬럼이 null => return true")
        void AccountTimezoneFieldIsNull() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone(null)  // 타임존이 null로 설정됨
                    .updateDeferredUntil(null)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone);

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("기존 타임존이랑 비교해서 2시간 이상이면 => return true")
        void TimezoneBiggerThanEqual2Hour() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("America/New_York")  // 시간대 차이를 두어 설정
                    .updateDeferredUntil(null)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone);

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("기존 타임존이랑 비교해서 2시간 미만이면 => return false")
        void TimezoneLessThan2Hour() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("Asia/Tokyo")
                    .updateDeferredUntil(null)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // when
            boolean result = accountTimezoneService.isTimezoneUpdateRequired(accountId, currentTimezone);

            // then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("updateTimezoneAndDeferredTest")
    class updateTimezoneAndDeferredTest {

        @Test
        @DisplayName("계정이 없을때 => throw AccountNotFoundException()")
        void NotExistAccount() {
            // given
            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            // then
            assertThrows(AccountNotFoundException.class, () ->
                    accountTimezoneService.updateTimezoneAndDeferred(accountId, currentTimezone, true));
        }

        @Test
        @DisplayName("타임존과 유보 시간을 모두 업데이트")
        void updateBothTimezoneAndDeferred() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("Asia/Tokyo")
                    .updateDeferredUntil(null)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

            // when
            accountTimezoneService.updateTimezoneAndDeferred(accountId, currentTimezone, true);

            // then
            verify(accountRepository, times(1)).save(accountCaptor.capture()); // save 메서드가 호출되었는지 확인
            Account savedAccount = accountCaptor.getValue();

            // 업데이트된 상태 검증
            assertEquals(currentTimezone, savedAccount.getTimezone());  // 타임존이 업데이트되었는지 확인
            assertNotNull(savedAccount.getUpdateDeferredUntil());       // 유보 시간이 설정되었는지 확인
        }

        @Test
        @DisplayName("타임존만 업데이트")
        void updateOnlyTimezone() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("Asia/Tokyo")
                    .updateDeferredUntil(null)
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

            // when
            accountTimezoneService.updateTimezoneAndDeferred(accountId, currentTimezone, false);

            // then
            verify(accountRepository, times(1)).save(accountCaptor.capture()); // save 메서드가 호출되었는지 확인
            Account savedAccount = accountCaptor.getValue();

            assertEquals(currentTimezone, savedAccount.getTimezone());  // 타임존이 업데이트되었는지 확인
            assertNull(savedAccount.getUpdateDeferredUntil());  // 유보 시간은 그대로 유지
        }

        @Test
        @DisplayName("유보 시간만 업데이트")
        void updateOnlyDeferred() {
            // given
            Account account = Account.builder()
                    .accountId(accountId)
                    .timezone("Asia/Tokyo")  // 타임존이 이미 설정됨
                    .updateDeferredUntil(null)  // 유보 시간이 설정되지 않음
                    .build();

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

            // when
            accountTimezoneService.updateTimezoneAndDeferred(accountId, null, true);

            // then
            verify(accountRepository, times(1)).save(accountCaptor.capture()); // save 메서드가 호출되었는지 확인
            Account savedAccount = accountCaptor.getValue();

            assertEquals(account.getTimezone(), savedAccount.getTimezone());  // 타임존은 변경되지 않음
            assertNotNull(savedAccount.getUpdateDeferredUntil());  // 유보 시간이 새로 설정됨
        }
    }

    @Test
    @DisplayName("모든 타임존 목록을 반환하고 정렬되어 있는지 확인")
    void getAllTimezonesTest() {
        // when
        List<String> timezones = accountTimezoneService.getAllTimezones();

        // then
        assertNotNull(timezones);  // 목록이 null이 아닌지 확인
        assertFalse(timezones.isEmpty());  // 목록이 빈 목록이 아닌지 확인

        // 목록이 정렬되어 있는지 확인
        List<String> sortedTimezones = timezones.stream().sorted().toList();
        assertEquals(sortedTimezones, timezones);  // 정렬된 목록과 동일한지 확인
    }
}
