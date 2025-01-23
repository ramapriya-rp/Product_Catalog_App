package com.rpms.productcatalog.controller;

import com.rpms.productcatalog.model.Product;
import com.rpms.productcatalog.model.ProductListResponse;
import com.rpms.productcatalog.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${messages.noProductFoundMessage}")
    private String noProductFoundMessage;

    @Value("${defaults.pageNo}")
    private int defaultPageNo;

    @Value("${defaults.pageSize}")
    private int defaultPageSize;

    private final Logger LOGGER =
            LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/products")
    public ResponseEntity<Object> addProduct(@Valid @RequestBody Product product) {
        LOGGER.info("Request received: Inside addProduct in ProductController");
        productService.addProduct(product);
        LOGGER.info("Product created successfully.");
        return new ResponseEntity<Object>("Product created successfully", HttpStatus.CREATED);
    }


    @GetMapping("/products")
    public ResponseEntity<Object> getAllProduct(
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize) {

        LOGGER.info("Request received: Inside getAllProducts in ProductController");

        int effectivePageNo = pageNo != null ? pageNo : defaultPageNo;
        int effectivePageSize = pageSize != null ? pageSize : defaultPageSize;
        LOGGER.info("Page No: {} page Size: {}", effectivePageNo, effectivePageSize);

        Pageable pageable = PageRequest.of(effectivePageNo, effectivePageSize);
        Page<Product> productPage = productService.getAllProducts(pageable);

        //if no products are available
        if (productPage.isEmpty()) {
            String message;
            if (pageNo != null && pageSize != null) {
                message = String.format(noProductFoundMessage + " for the given page no: %d and pageSize: %d", pageNo, pageSize);
            } else {
                message = noProductFoundMessage;
            }
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        //if products are available and are retrieved
        return ResponseEntity.ok(new ProductListResponse(
                productPage.getContent(),
                productPage.getTotalPages(),
                productPage.getTotalElements()));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getProductByName(@RequestParam String query) {
        LOGGER.info("Request received: Inside getProductsByName in ProductController, input query is: {}", query);

        List<Product> productList =  productService.getProductByName(query);
        if (productList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format(noProductFoundMessage+" for the query: "+query));
        }
        return ResponseEntity.ok(productList);
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProductByID(@PathVariable("id") Long productID) {
        LOGGER.info("Request received: Inside getProductByID " +
                "in product controller, with ID: {}", productID);

        Product product = productService.getProductByID(productID);

        return product != null ? ResponseEntity.status(HttpStatus.OK).body(product) : ResponseEntity.status(HttpStatus.OK)
                .body(String.format(noProductFoundMessage + " for the id: %d", productID));
    }

}
