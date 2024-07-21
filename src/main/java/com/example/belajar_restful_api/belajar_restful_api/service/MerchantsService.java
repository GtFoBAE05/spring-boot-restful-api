package com.example.belajar_restful_api.belajar_restful_api.service;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.entity.Merchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.Users;
import com.example.belajar_restful_api.belajar_restful_api.jwt.JwtService;
import com.example.belajar_restful_api.belajar_restful_api.model.request.LoginRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.RegisterRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateUserRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.LoginResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.UsersResponse;
import com.example.belajar_restful_api.belajar_restful_api.repository.AuthMerchantRepository;
import com.example.belajar_restful_api.belajar_restful_api.repository.AuthUsersRepository;
import com.example.belajar_restful_api.belajar_restful_api.repository.MerchantsRepository;
import com.example.belajar_restful_api.belajar_restful_api.repository.UsersRepository;
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

@Service
public class MerchantsService  implements UserDetailsService {
    @Autowired
    private MerchantsRepository merchantsRepository;

    @Autowired
    private AuthMerchantRepository authMerchantRepository;

    @Autowired
    private ValidationService validationService;


    @Autowired
    private JwtService jwtService;

    @Transactional
    public void registerAsMerchant(RegisterRequest registerRequest){

        validationService.validate(registerRequest);

        if(authMerchantRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"Email already registered");
        }

        AuthMerchants authMerchants = new AuthMerchants();

        authMerchants.setEmail(registerRequest.getEmail());
        authMerchants.setPassword(BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt()));

        Merchants merchants = new Merchants();
        merchants.setName(registerRequest.getName());
        merchants.setAddress(registerRequest.getAddress());
        merchants.setGender(registerRequest.getGender());
        merchants.setPhoneNumber(registerRequest.getPhoneNumber());
        merchants.setDateOfBirth(registerRequest.getDateOfBirth());

        Merchants save = merchantsRepository.save(merchants);

        authMerchants.setMerchant(save);
        authMerchantRepository.save(authMerchants);

    }

    @Transactional
    public LoginResponse loginAsMerchant(LoginRequest loginRequest){
        validationService.validate(loginRequest);

        AuthMerchants authMerchants = authMerchantRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found")
        );

        if(BCrypt.checkpw(loginRequest.getPassword(), authMerchants.getPassword())){

            String jwtToken = jwtService.generateToken(authMerchants);
            long expirationTime = jwtService.getExpirationTime();

            authMerchants.setToken(jwtToken);
            authMerchants.setTokenExpireAt(expirationTime);

            authMerchantRepository.save(authMerchants);

            return LoginResponse.builder()
                    .token(jwtToken)
                    .expireAt(expirationTime)
                    .build();

        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found");
        }

    }

    @Transactional
    public UsersResponse getMerchantDetail(String email){
        AuthMerchants authMerchants = authMerchantRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found1")
        );

        Merchants merchants = merchantsRepository.findById(authMerchants.getMerchant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found2")
        );;

        return UsersResponse.builder()
                .name(merchants.getName())
                .email(authMerchants.getEmail())
                .address(merchants.getAddress())
                .dateOfBirth(merchants.getDateOfBirth())
                .gender(merchants.getGender())
                .phoneNumber(merchants.getPhoneNumber())
                .build();
    }


    @Transactional
    public void updateMerchant(String email, UpdateUserRequest updateUserRequest){
        validationService.validate(updateUserRequest);

        AuthMerchants authMerchants = authMerchantRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found1")
        );

        Merchants merchants = merchantsRepository.findById(authMerchants.getMerchant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found2")
        );


        if(Objects.nonNull(updateUserRequest.getName())){
            merchants.setName(updateUserRequest.getName());
        }

        if(Objects.nonNull(updateUserRequest.getAddress())){
            merchants.setAddress(updateUserRequest.getAddress());
        }

        if(Objects.nonNull(updateUserRequest.getGender())){
            merchants.setGender(updateUserRequest.getGender());
        }

        if(Objects.nonNull(updateUserRequest.getPhoneNumber())){
            merchants.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

        if(Objects.nonNull(updateUserRequest.getDateOfBirth())){
            merchants.setDateOfBirth(updateUserRequest.getDateOfBirth());
        }

        merchantsRepository.save(merchants);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AuthMerchants> userResult = authMerchantRepository.findByEmail(username);

        if(!userResult.isPresent()){
            throw new UsernameNotFoundException("User not registered");
        }

        return new User(userResult.get().getUsername(),  userResult.get().getPassword(), new ArrayList<>());
    }


    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
    }
}
