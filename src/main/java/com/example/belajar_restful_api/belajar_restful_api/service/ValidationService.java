package com.example.belajar_restful_api.belajar_restful_api.service;

import com.example.belajar_restful_api.belajar_restful_api.model.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class ValidationService {
    @Autowired
    private Validator validation;

    public void validate(Object request){
        Set<ConstraintViolation<Object>> constraintViolations = validation.validate(request);

        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

    }

}
