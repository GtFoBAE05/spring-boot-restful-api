package com.example.belajar_restful_api.belajar_restful_api.repository;

import com.example.belajar_restful_api.belajar_restful_api.entity.Orders;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, UUID> , JpaSpecificationExecutor<Orders> {
}
