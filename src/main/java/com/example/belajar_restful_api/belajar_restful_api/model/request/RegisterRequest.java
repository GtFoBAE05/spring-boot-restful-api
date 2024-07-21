package com.example.belajar_restful_api.belajar_restful_api.model.request;

import com.example.belajar_restful_api.belajar_restful_api.helper.custom_validator.CustomDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

//    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    @CustomDateConstraint
    private LocalDate dateOfBirth;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "male|female", message = "Gender must be either 'male' or 'female'")
    private String gender;

    @NotBlank
    private String address;

}
