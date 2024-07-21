package com.example.belajar_restful_api.belajar_restful_api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersHistoryForUserResponse {

    private String id;

    private String userId;

    private int quantity;

    private int productPrice;

    private int totalPrice;

    private String status;

    private String invoiceUrl;

    private LocalDateTime  createdAt;

    private LocalDateTime lastUpdatedAt;

    private ProductResponse productResponse;

    private UsersResponse usersResponse;


}
