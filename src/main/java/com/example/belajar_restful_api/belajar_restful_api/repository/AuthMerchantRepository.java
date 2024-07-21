package com.example.belajar_restful_api.belajar_restful_api.repository;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.entity.AuthUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthMerchantRepository extends JpaRepository<AuthMerchants, UUID> {
    Optional<AuthMerchants> findByEmail(String email);
}
