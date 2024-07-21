package com.example.belajar_restful_api.belajar_restful_api.controller;

import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity handleDateTimeParseException(DateTimeParseException ex) {
        String error = "Invalid format, pleas use yyyy-MM-dd";
        return ResponseEntity.badRequest().body(
                BasicResponse.builder().message(error).success(false).build()
        );
    }

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity constraintViolationException(ConstraintViolationException exception){
//        return ResponseEntity.badRequest().body(
//                new BasicResponse().builder().success(false).message(exception.getMessage().split(",")[0]).build()
//        );
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BasicResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String error = ex.getConstraintViolations().iterator().next().getMessage();


        return ResponseEntity.badRequest().body(
                BasicResponse.builder().message(ex.getMessage()).success(false).build()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity responseStatusException(ResponseStatusException exception){
        return ResponseEntity.status(exception.getStatusCode()).body(
                new BasicResponse().builder().success(false).message(exception.getReason()).build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        // TODO send this stack trace to an observability tool
        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return ResponseEntity.status(errorDetail.getStatus()).body(
                    new BasicResponse().builder().success(false).message(errorDetail.getDetail()).build()
            );
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }

        return ResponseEntity.status(errorDetail.getStatus()).body(
                new BasicResponse().builder().success(false).message(errorDetail.getDetail()).build()
        );
    }


}

