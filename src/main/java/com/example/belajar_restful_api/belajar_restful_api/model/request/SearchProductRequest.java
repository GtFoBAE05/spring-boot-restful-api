package com.example.belajar_restful_api.belajar_restful_api.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {

    private String name;

    private String category;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
