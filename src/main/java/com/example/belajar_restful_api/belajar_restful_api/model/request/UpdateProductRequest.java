package com.example.belajar_restful_api.belajar_restful_api.model.request;

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
public class UpdateProductRequest {

    @NotBlank
    private String productId;

    @NotBlank
    private String name;
    @NotBlank
    private String description;

    @NotNull
    @Min(1)
    private int price;

    @NotNull
    @Min(1)
    private int stock;

    @NotNull
    @Min(1)
    private int productCategoryId;
}
