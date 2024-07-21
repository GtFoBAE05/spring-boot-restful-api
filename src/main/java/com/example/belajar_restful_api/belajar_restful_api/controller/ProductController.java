package com.example.belajar_restful_api.belajar_restful_api.controller;

import com.example.belajar_restful_api.belajar_restful_api.entity.AuthMerchants;
import com.example.belajar_restful_api.belajar_restful_api.model.request.AddProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.SearchProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicResponse;
import com.example.belajar_restful_api.belajar_restful_api.model.response.BasicWithDataResponse;
import com.example.belajar_restful_api.belajar_restful_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(
            path = "/api/product/add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity addProduct(
            @RequestBody AddProductRequest addProductRequest
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthMerchants currentUser = (AuthMerchants) authentication.getPrincipal();

        productService.addProduct(currentUser.getEmail(), addProductRequest);

        return ResponseEntity.ok().body(
                new BasicResponse().builder().success(true).message("Success create product").build()
        );

    }

    @PutMapping(
            path = "/api/product/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    private ResponseEntity updateProduct(
            @RequestBody UpdateProductRequest updateProductRequest
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthMerchants currentUser = (AuthMerchants) authentication.getPrincipal();

        productService.updateProduct(currentUser.getEmail(), updateProductRequest);

        return ResponseEntity.ok().body(
                new BasicResponse().builder().success(true).message("Success update product").build()
        );

    }

    @GetMapping(
            path = "/api/product/{productId}"
    )
    @ResponseBody
    private ResponseEntity getSingleProduct(
            @PathVariable String productId
    ) {

        return ResponseEntity.ok().body(
                new BasicWithDataResponse().builder()
                        .success(true)
                        .message("Success get product")
                        .data(productService.getSingleProduct(productId))
                        .build()
        );

    }

    @GetMapping(
            path = "/api/product"
    )
    @ResponseBody
    private ResponseEntity SearchProduct(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page ,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
            ) {
        SearchProductRequest request = SearchProductRequest.builder()
                .name(name)
                .category(category)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok().body(
                new BasicWithDataResponse().builder()
                        .success(true)
                        .message("Success get product")
                        .data(productService.searchProductOrCategory(request))
                        .build()
        );

    }


}
