package com.example.belajar_restful_api.belajar_restful_api.service;

import com.example.belajar_restful_api.belajar_restful_api.entity.*;
import com.example.belajar_restful_api.belajar_restful_api.model.request.AddProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.SearchProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateProductRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.UpdateUserRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.ProductResponse;
import com.example.belajar_restful_api.belajar_restful_api.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private AuthMerchantRepository authMerchantRepository;

    @Autowired
    private MerchantsRepository merchantsRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void addProduct(String email, AddProductRequest addProductRequest) {
        validationService.validate(addProductRequest);

        AuthMerchants authMerchants = authMerchantRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password not found")
        );

        Merchants merchants = merchantsRepository.findById(authMerchants.getMerchant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password not found2")
        );

        ProductCategories productCategories = productCategoryRepository.findById(addProductRequest.getProductCategoryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product Category not found")
        );

        Product product = new Product();
        product.setMerchant(merchants);
        product.setName(addProductRequest.getName());
        product.setDescription(addProductRequest.getDescription());
        product.setPrice(addProductRequest.getPrice());
        product.setStock(addProductRequest.getStock());
        product.setProductCategories(productCategories);

        productRepository.save(product);

    }


    @Transactional
    public void updateProduct(String email,  UpdateProductRequest updateProductRequest) {
        validationService.validate(updateProductRequest);

        AuthMerchants authMerchants = authMerchantRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password not found")
        );

        Merchants merchants = merchantsRepository.findById(authMerchants.getMerchant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password not found2")
        );

        Product oldProduct = productRepository.findById(UUID.fromString(updateProductRequest.getProductId())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product  not found")
        );

        if (!merchants.getId().equals(oldProduct.getMerchant().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your product");
        }

        ProductCategories productCategories = productCategoryRepository.findById(updateProductRequest.getProductCategoryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product Category not found")
        );

        Product product = new Product();

        product.setId(oldProduct.getId());

        product.setMerchant(merchants);

        product.setName(updateProductRequest.getName());

        product.setDescription(updateProductRequest.getDescription());

        product.setPrice(updateProductRequest.getPrice());

        product.setStock(updateProductRequest.getStock());

        product.setProductCategories(productCategories);

        productRepository.save(product);

    }

    @Transactional
    public ProductResponse getSingleProduct(String productId) {


        Product oldProduct = productRepository.findById(UUID.fromString(productId)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product  not found")
        );

        ProductResponse productResponse = new ProductResponse();

        productResponse.setProductId(oldProduct.getId().toString());
        productResponse.setName(oldProduct.getName());
        productResponse.setProductCategory(oldProduct.getProductCategories().getName());
        productResponse.setDescription(oldProduct.getDescription());
        productResponse.setPrice(oldProduct.getPrice());
        productResponse.setStock(oldProduct.getStock());

       return productResponse;


    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProductOrCategory(SearchProductRequest searchProductRequest){

        Specification<Product> specification= (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(searchProductRequest.getName())){
                predicates.add(builder.or(
                        builder.like(root.get("name"), "%" + searchProductRequest.getName() + "%")
                ));
            }

            if (Objects.nonNull(searchProductRequest.getCategory())) {
                Join<Product, ProductCategories> categoryJoin = root.join("productCategories");
                predicates.add(builder.like(builder.lower(categoryJoin.get("name")), "%" + searchProductRequest.getCategory().toLowerCase() + "%"));
            }


            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

        };

        Pageable pageable = PageRequest.of(searchProductRequest.getPage(), searchProductRequest.getSize());

        Page<Product> products = productRepository.findAll(specification, pageable);

        List<ProductResponse> productResponses = products.getContent().stream()
                        .map(this ::convertToProductResponse )
                        .toList();

        return new PageImpl<>(productResponses, pageable, products.getTotalElements());

    }

    private ProductResponse convertToProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setName(product.getName());
        productResponse.setProductId(product.getId().toString());
        productResponse.setDescription(product.getDescription());
        productResponse.setStock(product.getStock());
        productResponse.setPrice(product.getPrice());
        productResponse.setProductCategory(product.getProductCategories().getName());
        productResponse.setMerchantName(product.getMerchant().getName());

        return productResponse;
    }


}
