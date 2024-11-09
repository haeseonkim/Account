package com.wev.account.exception;

import java.time.ZonedDateTime;

public class TimezoneUpdateDeferredException extends RuntimeException {
    public TimezoneUpdateDeferredException(ZonedDateTime deferredUntil) {
        super("Timezone update is deferred until " + deferredUntil);
    }
}