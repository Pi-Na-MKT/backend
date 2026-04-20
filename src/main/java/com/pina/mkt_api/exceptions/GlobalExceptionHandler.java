package com.pina.mkt_api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex,
            HttpServletRequest request
    ) {

        ErrorResponse error = new ErrorResponse(
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                return ErrorResponse.super.getHeaders();
            }

            @Override
            public ProblemDetail getBody() {
                return null;
            }

            @Override
            public String getTypeMessageCode() {
                return ErrorResponse.super.getTypeMessageCode();
            }

            @Override
            public String getTitleMessageCode() {
                return ErrorResponse.super.getTitleMessageCode();
            }

            @Override
            public String getDetailMessageCode() {
                return ErrorResponse.super.getDetailMessageCode();
            }

            @Override
            public Object @Nullable [] getDetailMessageArguments() {
                return ErrorResponse.super.getDetailMessageArguments();
            }

            @Override
            public Object @Nullable [] getDetailMessageArguments(MessageSource messageSource, Locale locale) {
                return ErrorResponse.super.getDetailMessageArguments(messageSource, locale);
            }

            @Override
            public ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
                return ErrorResponse.super.updateAndGetBody(messageSource, locale);
            }
        };

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        ErrorResponse error = new ErrorResponse(
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public ProblemDetail getBody() {
                return null;
            }
        };

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        ErrorResponse error = new ErrorResponse(
        ) {
            @Override
            public HttpStatusCode getStatusCode() {
                return null;
            }

            @Override
            public ProblemDetail getBody() {
                return null;
            }
        };

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}