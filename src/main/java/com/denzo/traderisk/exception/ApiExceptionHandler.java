package com.denzo.traderisk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RiskViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleRiskViolation(RiskViolationException ex) {
        return Map.of("error", ex.getMessage());
    }
}
