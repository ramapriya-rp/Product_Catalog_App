package com.rpms.productcatalog.service;

import com.rpms.productcatalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    boolean addProduct(Product product);

    Product getProductByID(long productID);

    Page<Product> getAllProducts(Pageable pageable);

    List<Product> getProductByName(String query);
}
