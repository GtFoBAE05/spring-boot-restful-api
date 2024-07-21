package com.example.belajar_restful_api.belajar_restful_api;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import com.example.belajar_restful_api.belajar_restful_api.repository.AuthMerchantRepository;
import com.example.belajar_restful_api.belajar_restful_api.repository.AuthUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableJpaAuditing
public class ApplicationConfiguration {
    @Autowired
    private  AuthUsersRepository authUsersRepository;

    @Autowired
    private AuthMerchantRepository authMerchantRepository;

    @Bean
    UserDetailsService userDetailsService() {

        return username -> {
            UserDetails userDetails = authUsersRepository.findByEmail(username)
                    .orElse(null);

            if (Objects.nonNull(userDetails)) {
                return userDetails;
            }

            userDetails = authMerchantRepository.findByEmail(username)
                    .orElse(null);

            if (Objects.nonNull(userDetails)) {
                return userDetails;
            }

            throw new UsernameNotFoundException("User not found");
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}