package com.example.belajar_restful_api.belajar_restful_api.repository;

import com.example.belajar_restful_api.belajar_restful_api.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {


}
