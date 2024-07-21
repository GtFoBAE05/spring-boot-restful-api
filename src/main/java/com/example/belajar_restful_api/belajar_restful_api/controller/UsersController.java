package com.example.belajar_restful_api.belajar_restful_api.controller;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.jwt.JwtService;
import com.example.belajar_restful_api.belajar_restful_api.model.request.LoginRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.RegisterRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateUserRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicWithDataResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.LoginResponse;
import com.example.belajar_restful_api.belajar_restful_api.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtService jwtService;


    @PostMapping(
            path = "/api/user/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity registerAsUser(
            @RequestBody RegisterRequest registerRequest
            ){

        usersService.registerAsUser(registerRequest);

        return ResponseEntity.ok().body(
                new BasicResponse().builder().success(true).message("Success Registered as User").build()
        );

    }

    @PostMapping(
            path = "/api/user/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity loginUser(
            @RequestBody LoginRequest loginRequest
    ){

        LoginResponse login = usersService.login(loginRequest);

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
            path = "/api/user/me"
    )
    @ResponseBody
    private ResponseEntity getUserDetail(
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUsers currentUser = (AuthUsers) authentication.getPrincipal();

        BasicWithDataResponse basicWithDataResponse = new BasicWithDataResponse();

        basicWithDataResponse.setSuccess(true);
        basicWithDataResponse.setMessage("Success get your detail");
        basicWithDataResponse.setData(
                usersService.getUserDetail(currentUser.getEmail())
        );


        return ResponseEntity.ok()
                .body(
                        basicWithDataResponse
                );

    }

    @PatchMapping(
            path = "/api/user/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity updateUserDetail(
            @RequestBody UpdateUserRequest updateUserRequest
            ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUsers currentUser = (AuthUsers) authentication.getPrincipal();

        usersService.updateUser(currentUser.getEmail(), updateUserRequest);

        BasicResponse basicWithDataResponse = new BasicResponse();

        basicWithDataResponse.setSuccess(true);
        basicWithDataResponse.setMessage("Success update detail");;


        return ResponseEntity.ok()
                .body(
                        basicWithDataResponse
                );

    }

    @PostMapping(
            path = "/api/auth/ping"
    )
    @ResponseBody
    private ResponseEntity ping(
    ){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthUsers currentUser = (AuthUsers) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);

    }

}

