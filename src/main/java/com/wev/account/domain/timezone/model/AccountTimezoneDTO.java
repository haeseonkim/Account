package com.wev.account.domain.timezone.model;

import java.time.ZonedDateTime;

public interface AccountTimezoneDTO {
    Long getAccountId();
    String getTimezone();
    ZonedDateTime getUpdateDeferredUntil();
}