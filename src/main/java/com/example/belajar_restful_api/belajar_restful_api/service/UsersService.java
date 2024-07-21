package com.example.belajar_restful_api.belajar_restful_api.service;


import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.entity.Users;
import com.example.belajar_restful_api.belajar_restful_api.jwt.JwtService;
import com.example.belajar_restful_api.belajar_restful_api.model.request.LoginRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.RegisterRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateUserRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.LoginResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.UsersResponse;
import com.example.belajar_restful_api.belajar_restful_api.repository.AuthUsersRepository;
import com.example.belajar_restful_api.belajar_restful_api.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UsersService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AuthUsersRepository authUsersRepository;

    @Autowired
    private ValidationService validationService;


    @Autowired
    private JwtService jwtService;

    @Transactional
    public void registerAsUser(RegisterRequest registerRequest){

        validationService.validate(registerRequest);

        if(authUsersRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Email already registered");
        }

        AuthUsers authUsers = new AuthUsers();

        authUsers.setEmail(registerRequest.getEmail());
        authUsers.setPassword(BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt()));

        Users user = new Users();
        user.setName(registerRequest.getName());
        user.setAddress(registerRequest.getAddress());
        user.setGender(registerRequest.getGender());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setDateOfBirth(registerRequest.getDateOfBirth());

        Users save = usersRepository.save(user);
        log.info(save.getId().toString());

        authUsers.setUser(save);
        authUsersRepository.save(authUsers);

    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest){
        validationService.validate(loginRequest);

        AuthUsers authUsers = authUsersRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found")
        );

        if(BCrypt.checkpw(loginRequest.getPassword(), authUsers.getPassword())){

            String jwtToken = jwtService.generateToken(authUsers);
            long expirationTime = jwtService.getExpirationTime();

            authUsers.setToken(jwtToken);
            authUsers.setTokenExpireAt(expirationTime);

            authUsersRepository.save(authUsers);

            return LoginResponse.builder()
                    .token(jwtToken)
                    .expireAt(expirationTime)
                    .build();

        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found");
        }

    }

    @Transactional
    public UsersResponse getUserDetail(String email){
        AuthUsers authUsers = authUsersRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found1")
        );

        Users user = usersRepository.findById(authUsers.getUser().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found2")
        );

        return UsersResponse.builder()
                .name(user.getName())
                .email(authUsers.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }


    @Transactional
    public void updateUser(String email, UpdateUserRequest updateUserRequest){
        validationService.validate(updateUserRequest);

        AuthUsers authUsers = authUsersRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found1")
        );

        Users user = usersRepository.findById(authUsers.getUser().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found2")
        );


        if(Objects.nonNull(updateUserRequest.getName())){
            user.setName(updateUserRequest.getName());
        }

        if(Objects.nonNull(updateUserRequest.getAddress())){
            user.setAddress(updateUserRequest.getAddress());
        }

        if(Objects.nonNull(updateUserRequest.getGender())){
            user.setGender(updateUserRequest.getGender());
        }

        if(Objects.nonNull(updateUserRequest.getPhoneNumber())){
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

        if(Objects.nonNull(updateUserRequest.getDateOfBirth())){
            user.setDateOfBirth(updateUserRequest.getDateOfBirth());
        }

        usersRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AuthUsers> userResult = authUsersRepository.findByEmail(username);

        if(!userResult.isPresent()){
            throw new UsernameNotFoundException("User not registered");
        }

        return new User(userResult.get().getUsername(),  userResult.get().getPassword(), new ArrayList<>());
    }


    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
    }


}
