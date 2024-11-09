package com.wev.account.config;

import com.wev.account.exception.AccountNotFoundException;
import com.wev.account.exception.MissingHeaderException;
import com.wev.account.exception.TimezoneUpdateDeferredException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @ExceptionHandler(MissingHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMissingHeaderException(MissingHeaderException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}