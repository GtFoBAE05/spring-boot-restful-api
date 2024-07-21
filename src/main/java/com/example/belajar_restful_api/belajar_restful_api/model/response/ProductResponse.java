package com.example.belajar_restful_api.belajar_restful_api.model.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private String productId;

    private String name;
    private String description;

    private int price;

    private int stock;

    private String productCategory;

    private String merchantName;
}
