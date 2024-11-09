package com.wev.account.exception;

public class MissingHeaderException extends RuntimeException {
    public MissingHeaderException(String headerName) {
        super("Required header is missing or empty: " + headerName);
    }
}
