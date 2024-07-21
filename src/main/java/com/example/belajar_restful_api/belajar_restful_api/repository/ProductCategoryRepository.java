package com.example.belajar_restful_api.belajar_restful_api.repository;

import com.example.belajar_restful_api.belajar_restful_api.entity.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategories, Integer> {
}
