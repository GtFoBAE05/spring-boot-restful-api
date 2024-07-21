package com.example.belajar_restful_api.belajar_restful_api.controller;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.jwt.JwtService;
import com.example.belajar_restful_api.belajar_restful_api.model.request.LoginRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.RegisterRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateUserRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicWithDataResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.LoginResponse;
import com.example.belajar_restful_api.belajar_restful_api.service.MerchantsService;
import com.example.belajar_restful_api.belajar_restful_api.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class MerchantsController {
    @Autowired
    private MerchantsService merchantsService;

    @Autowired
    private JwtService jwtService;


    @PostMapping(
            path = "/api/merchant/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity registerAsMerchant(
            @RequestBody RegisterRequest registerRequest
    ){

        merchantsService.registerAsMerchant(registerRequest);

        return ResponseEntity.ok().body(
                new BasicResponse().builder().success(true).message("Success Registered as Merchant").build()
        );

    }

    @PostMapping(
            path = "/api/merchant/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity loginMerchant(
            @RequestBody LoginRequest loginRequest
    ){

        LoginResponse login = merchantsService.loginAsMerchant(loginRequest);

        return ResponseEntity.ok().body(
                new BasicWithDataResponse()
                        .builder()
                        .success(true)
                        .message("Success login")
                        .data(login)
                        .build()
        );

    }

    @GetMapping(
            path = "/api/merchant/me"
    )
    @ResponseBody
    private ResponseEntity getMerchantDetail(
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthMerchants currentUser = (AuthMerchants) authentication.getPrincipal();

        BasicWithDataResponse basicWithDataResponse = new BasicWithDataResponse();

        basicWithDataResponse.setSuccess(true);
        basicWithDataResponse.setMessage("Success get your detail");
        basicWithDataResponse.setData(
                merchantsService.getMerchantDetail(currentUser.getEmail())
        );


        return ResponseEntity.ok()
                .body(
                        basicWithDataResponse
                );

    }

    @PatchMapping(
            path = "/api/merchant/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity updateMerchantDetail(
            @RequestBody UpdateUserRequest updateUserRequest
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthMerchants currentUser = (AuthMerchants) authentication.getPrincipal();

        merchantsService.updateMerchant(currentUser.getEmail(), updateUserRequest);

        BasicResponse basicWithDataResponse = new BasicResponse();

        basicWithDataResponse.setSuccess(true);
        basicWithDataResponse.setMessage("Success update detail");;


        return ResponseEntity.ok()
                .body(
                        basicWithDataResponse
                );

    }

}
