package com.example.belajar_restful_api.belajar_restful_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_categories")
public class ProductCategories {

    @Id
    private int id;

    private String name;

    @OneToMany(mappedBy = "productCategories", cascade = CascadeType.ALL)
    private List<Product> product;
}
