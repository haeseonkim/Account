package com.wev.account.exception;

public class TimezoneNotFoundException extends RuntimeException {
    public TimezoneNotFoundException() {
        super("Timezone is required but was null.");
    }
}
