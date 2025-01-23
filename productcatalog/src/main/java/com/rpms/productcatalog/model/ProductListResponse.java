package com.rpms.productcatalog.model;

import java.util.List;

public class ProductListResponse {
    private List<Product> products;
    private int totalPages;
    private long totalProducts;

    // Constructors
    public ProductListResponse(List<Product> products, int totalPages, long totalProducts) {
        this.products = products;
        this.totalPages = totalPages;
        this.totalProducts = totalProducts;
    }

    // Getters and Setters
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }


    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }
}
