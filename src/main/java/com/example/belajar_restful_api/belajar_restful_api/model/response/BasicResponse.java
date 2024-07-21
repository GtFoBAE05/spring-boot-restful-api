package com.example.belajar_restful_api.belajar_restful_api.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicResponse{

    private boolean success;

    private String message;

}
