package com.example.belajar_restful_api.belajar_restful_api.service;

import com.example.belajar_restful_api.belajar_restful_api.entity.*;
import com.example.belajar_restful_api.belajar_restful_api.model.request.OrderRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.request.XenditInvoiceRequest;
import com.example.belajar_restful_api.belajar_restful_api.model.response.*;
import com.example.belajar_restful_api.belajar_restful_api.repository.*;
import com.example.belajar_restful_api.belajar_restful_api.xendit.XenditService;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import jakarta.persistence.PostPersist;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
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

import java.util.*;

@Service
@Slf4j
public class OrdersService {

    @Autowired
    private AuthUsersRepository authUsersRepository;

    @Autowired
    private MerchantsRepository merchantsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private XenditService xenditService;

    @Transactional
    @PostPersist
    public OrdersResponse addOrders(String userId, String email, OrderRequest orderRequest) throws XenditException {

        Product product = productRepository.findById(UUID.fromString(orderRequest.getProductId())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product  not found")
        );

        Merchants merchants = merchantsRepository.findById(product.getMerchant().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password not found2")
        );

        Users user = usersRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Email or Password not found2")
        );

        if(orderRequest.getQuantity()>product.getStock()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product stock not enough");
        }


        Orders orders = new Orders();

        orders.setMerchant(merchants);
        orders.setUser(user);
        orders.setProduct(product);
        orders.setQuantity(orderRequest.getQuantity());
        orders.setTotalPrice(product.getPrice() * orderRequest.getQuantity());
        orders.setStatus("PENDING");
        Orders orderResult = ordersRepository.save(orders);
        ordersRepository.flush();



        Invoice invoice = xenditService.createInvoice(
                orderResult.getId().toString(),
                product.getPrice() * orderRequest.getQuantity(),
                email,
                product.getDescription()
        );

        Orders orderNew = ordersRepository.findById(orderResult.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"wrong order id")
        );

        orderNew.setInvoiceUrl(invoice.getInvoiceUrl());

        orderNew.setCreatedAt(orderResult.getCreatedAt());
        ordersRepository.save(orderNew);


        product.setStock(product.getStock() - orderRequest.getQuantity());
        productRepository.save(product);



        return new OrdersResponse().builder().invoiceUrl(invoice.getInvoiceUrl()).build();


    }


    @Transactional
    public void updateInvoiceStatus(XenditInvoiceRequest xenditInvoiceRequest){

        Orders order = ordersRepository.findById(UUID.fromString(xenditInvoiceRequest.getExternalId())).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wrong id")
        );

        order.setStatus(xenditInvoiceRequest.getStatus());

    }


    @Transactional
    public Page<OrdersHistoryForUserResponse> getOrderHistoryForUsers(UUID userId, String status, int page, int size){

        Specification<Orders> specification= (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Orders, Users> categoryJoin = root.join("user");
            predicates.add(builder.or(
                    builder.equal(categoryJoin.get("id"), userId)
            ));

            if(Objects.nonNull(status)){
                predicates.add(builder.or(
                        builder.like(root.get("status"), "%" + status + "%")
                ));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

        };

        Pageable pageable = PageRequest.of(page, size);

        Page<Orders> orders = ordersRepository.findAll(specification, pageable);

        List<OrdersHistoryForUserResponse> ordersHistoryForUserResponses = orders.getContent().stream()
                .map(this ::convertToOrdersHistoryForUserResponse )
                .toList();

        return new PageImpl<>(ordersHistoryForUserResponses, pageable, orders.getTotalElements());


    }

    @Transactional
    public Page<OrdersHistoryForMerchantResponse> getOrderHistoryForMerchant(UUID userId, String status, int page, int size){

        Specification<Orders> specification= (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Orders, Users> categoryJoin = root.join("merchant");
            predicates.add(builder.or(
                    builder.equal(categoryJoin.get("id"), userId)
            ));

            if(Objects.nonNull(status)){
                predicates.add(builder.or(
                        builder.like(root.get("status"), "%" + status + "%")
                ));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

        };

        Pageable pageable = PageRequest.of(page, size);

        Page<Orders> orders = ordersRepository.findAll(specification, pageable);

        List<OrdersHistoryForMerchantResponse> ordersHistoryForMerchantResponses = orders.getContent().stream()
                .map(this ::convertToOrdersHistoryForMerchantResponse )
                .toList();

        return new PageImpl<>(ordersHistoryForMerchantResponses, pageable, orders.getTotalElements());


    }

    private OrdersHistoryForUserResponse convertToOrdersHistoryForUserResponse(Orders orders) {
        OrdersHistoryForUserResponse ordersHistoryForUserResponse = new OrdersHistoryForUserResponse();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(orders.getProduct().getId().toString());
        productResponse.setProductCategory(orders.getProduct().getProductCategories().getName());
        productResponse.setName(orders.getProduct().getName());
        productResponse.setDescription(orders.getProduct().getDescription());
        productResponse.setPrice(orders.getProduct().getPrice());

        UsersResponse usersResponse = new UsersResponse();
        usersResponse.setName(orders.getMerchant().getName());

        ordersHistoryForUserResponse.setId(orders.getId().toString());
        ordersHistoryForUserResponse.setQuantity(orders.getQuantity());
        ordersHistoryForUserResponse.setInvoiceUrl(orders.getInvoiceUrl());
        ordersHistoryForUserResponse.setCreatedAt(orders.getCreatedAt());
        ordersHistoryForUserResponse.setLastUpdatedAt(orders.getLastUpdatedAt());
        ordersHistoryForUserResponse.setStatus(orders.getStatus());
        ordersHistoryForUserResponse.setTotalPrice(orders.getTotalPrice());
        ordersHistoryForUserResponse.setUserId(orders.getUser().getId().toString());
        ordersHistoryForUserResponse.setProductPrice(orders.getProduct().getPrice());
        ordersHistoryForUserResponse.setProductResponse(productResponse);
        ordersHistoryForUserResponse.setUsersResponse(usersResponse);

        return ordersHistoryForUserResponse;
    }

    private OrdersHistoryForMerchantResponse convertToOrdersHistoryForMerchantResponse(Orders orders) {
        OrdersHistoryForMerchantResponse ordersHistoryForUserResponse = new OrdersHistoryForMerchantResponse();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(orders.getProduct().getId().toString());
        productResponse.setProductCategory(orders.getProduct().getProductCategories().getName());
        productResponse.setName(orders.getProduct().getName());
        productResponse.setDescription(orders.getProduct().getDescription());
        productResponse.setPrice(orders.getProduct().getPrice());


        ordersHistoryForUserResponse.setId(orders.getId().toString());
        ordersHistoryForUserResponse.setQuantity(orders.getQuantity());
        ordersHistoryForUserResponse.setInvoiceUrl(orders.getInvoiceUrl());
        ordersHistoryForUserResponse.setCreatedAt(orders.getCreatedAt());
        ordersHistoryForUserResponse.setLastUpdatedAt(orders.getLastUpdatedAt());
        ordersHistoryForUserResponse.setStatus(orders.getStatus());
        ordersHistoryForUserResponse.setTotalPrice(orders.getTotalPrice());
        ordersHistoryForUserResponse.setUserId(orders.getUser().getId().toString());
        ordersHistoryForUserResponse.setProductPrice(orders.getProduct().getPrice());
        ordersHistoryForUserResponse.setProductResponse(productResponse);

        return ordersHistoryForUserResponse;
    }


}
