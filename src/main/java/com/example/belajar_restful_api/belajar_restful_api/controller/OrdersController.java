package com.example.belajar_restful_api.belajar_restful_api.controller;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.model.request.OrderRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.XenditInvoiceRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.*;
import com.example.belajar_restful_api.belajar_restful_api.service.OrdersService;
import com.xendit.exception.XenditException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping(
            path = "/api/order",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity addOrders(
            @RequestBody OrderRequest orderRequest
            ) throws XenditException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUsers currentUser = (AuthUsers) authentication.getPrincipal();

        OrdersResponse ordersResponse = ordersService.addOrders(currentUser.getUser().getId().toString(), currentUser.getEmail(), orderRequest);

        BasicWithDataResponse basicWithDataResponse = new BasicWithDataResponse<>();

        basicWithDataResponse.setData(
                ordersResponse
        );

        basicWithDataResponse.setSuccess(true);

        basicWithDataResponse.setMessage("Success create order");

        return ResponseEntity.ok().body(basicWithDataResponse);

    }

    @PostMapping(
            path = "/api/xendit/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity updateInvoiceStatus(
            @RequestBody XenditInvoiceRequest xenditInvoiceRequest
    ) throws XenditException {

        ordersService.updateInvoiceStatus(xenditInvoiceRequest);

        BasicResponse basicResponse = new BasicResponse();

        basicResponse.setSuccess(true);

        basicResponse.setMessage("Success update order");

        return ResponseEntity.ok().body(basicResponse);

    }

    @GetMapping(
            path = "/api/user/order",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity getOrdersHistoryForUser(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page ,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUsers currentUser = (AuthUsers) authentication.getPrincipal();

        Page<OrdersHistoryForUserResponse> orderHistory = ordersService.getOrderHistoryForUsers(currentUser.getUser().getId(), status, page, size);

        BasicWithDataResponse basicWithDataResponse = new BasicWithDataResponse<>();

        basicWithDataResponse.setData(
                orderHistory
        );

        basicWithDataResponse.setSuccess(true);

        basicWithDataResponse.setMessage("Success get order");

        return ResponseEntity.ok().body(basicWithDataResponse);

    }

    @GetMapping(
            path = "/api/merchant/order",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity getOrdersHistoryForMerchant(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page ,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthMerchants currentUser = (AuthMerchants) authentication.getPrincipal();

        Page<OrdersHistoryForMerchantResponse> orderHistory = ordersService.getOrderHistoryForMerchant(currentUser.getMerchant().getId(), status, page, size);

        BasicWithDataResponse basicWithDataResponse = new BasicWithDataResponse<>();

        basicWithDataResponse.setData(
                orderHistory
        );

        basicWithDataResponse.setSuccess(true);

        basicWithDataResponse.setMessage("Success get order");

        return ResponseEntity.ok().body(basicWithDataResponse);

    }

}
