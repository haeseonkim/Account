package com.wev.account.domain.timezone.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId + " not found");
    }
}