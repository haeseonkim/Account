package com.wev.account.config;

import com.wev.account.domain.timezone.exception.AccountNotFoundException;
import com.wev.account.domain.timezone.exception.TimezoneUpdateDeferredException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAccountNotFoundException(AccountNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TimezoneUpdateDeferredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleTimezoneUpdateDeferredException(TimezoneUpdateDeferredException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex) {
        return "An unexpected error occurred: " + ex.getMessage();
    }
}