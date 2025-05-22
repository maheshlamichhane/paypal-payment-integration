package com.paypal.integration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getMessage(),String.valueOf(e.getStatus().value())));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(PaymentException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getMessage(),String.valueOf(e.getStatus().value())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage(),String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }


    private static record ErrorResponse(String message,String errorCode){};
}
