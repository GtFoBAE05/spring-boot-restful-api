package com.example.belajar_restful_api.belajar_restful_api.repository;

import com.example.belajar_restful_api.belajar_restful_api.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantsRepository extends JpaRepository<Merchants, UUID> {
}
