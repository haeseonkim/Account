package com.wev.account.exception;

public class InvalidTimezoneOrDeferredException extends RuntimeException {
    public InvalidTimezoneOrDeferredException(String message) {
        super(message);
    }
}