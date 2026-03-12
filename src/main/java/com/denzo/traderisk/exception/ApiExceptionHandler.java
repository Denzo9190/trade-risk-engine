package com.denzo.traderisk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RiskViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleRiskViolation(RiskViolationException ex) {
        return ex.getMessage();
    }
}
