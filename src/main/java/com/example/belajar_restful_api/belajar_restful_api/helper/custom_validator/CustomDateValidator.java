package com.example.belajar_restful_api.belajar_restful_api.helper.custom_validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomDateValidator implements
        ConstraintValidator<CustomDateConstraint, LocalDate> {

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public void initialize(CustomDateConstraint customDate) {
    }

    @Override
    public boolean isValid(LocalDate customDateField,
                           ConstraintValidatorContext cxt) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            String dateString = customDateField.format(formatter);
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            return customDateField.equals(parsedDate);
        } catch (Exception e) {
            return false;
        }
    }

}
