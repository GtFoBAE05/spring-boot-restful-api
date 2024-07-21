package com.example.belajar_restful_api.belajar_restful_api.model.request;

import com.example.belajar_restful_api.belajar_restful_api.helper.custom_validator.CustomDateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String phoneNumber;

    @Pattern(regexp = "male|female", message = "Gender must be either 'male' or 'female'")
    private String gender;

    private String address;
}
