package com.rpms.productcatalog.service;

import com.rpms.productcatalog.controller.ProductController;
import com.rpms.productcatalog.model.Product;
import com.rpms.productcatalog.utils.DamerauLevenshteinDistance;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final Map<Long, Product> productMapList = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();
    private final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Value("${product.csv.file}")
    private String csvFileName;

    @Value("${normalizedDistanceThreshold}")
    private double normalizedDistanceThreshold;

    @Override
    public boolean addProduct(Product product) {
        product.setId(idCounter.incrementAndGet());
        productMapList.put(product.getId(), product);
        return true;
    }

    @Override
    public Product getProductByID(long productID) {
        return Optional.ofNullable(productMapList.get(productID))
                .map(product -> {
                    LOGGER.info("Success: Product found for ID: {}", productID);
                    return product;
                })
                .orElseGet(() -> {
                    LOGGER.info("No product found for ID: {}", productID);
                    return null;
                });
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        List<Product> productList = new ArrayList<>(productMapList.values());

        // Handle no products in the system
        if (productList.isEmpty()) {
            LOGGER.warn("ATTENTION!!! No products are available in the system.");
            // Returning an empty page with a message logged
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // Pagination logic
        int totalProducts = productList.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), productList.size());

        // Handle invalid page numbers
        if (start >= totalProducts) {
            int totalPages = (int) Math.ceil((double) totalProducts / pageable.getPageSize());
            String message = String.format("Requested page number %d is out of range. " +
                            "Valid page numbers are from 0 to %d. Total no of Products available: %d",
                    pageable.getPageNumber(), totalPages-1, totalProducts);
            LOGGER.warn(message);
            // Returning an empty page with a message logged
            return new PageImpl<>(Collections.emptyList(), pageable, totalProducts);
        }

        // Return paginated product list
        List<Product> pagedProductList = productList.subList(start, end);
        LOGGER.info("Products retrieved successfully");
        return new PageImpl<>(pagedProductList, pageable, totalProducts);
    }

    @Override
    @Cacheable(value = "getProductByNameCache", key = "#query")
    public List<Product> getProductByName(String query) {
        // Convert query to lowercase for case-insensitive comparison
        String lowerCaseQuery = query.toLowerCase();

        // Load all products from productMapList
        List<Product> productList = new ArrayList<>(productMapList.values());

        if (productList.isEmpty()) {
            LOGGER.info("ATTENTION!!! No products are available in the system");
            return Collections.emptyList();
        }
        // Damerau-Levenshtein logic
        DamerauLevenshteinDistance distanceCalculator = new DamerauLevenshteinDistance();

        // Filter each products based on substring match or normalized Damerau-Levenshtein distance
        List<Product> matchedProducts = productList.stream()
                .filter(product -> {
                    String productName = product.getName().toLowerCase();

                    // Substring check
                    boolean isSubstring = productName.contains(lowerCaseQuery);

                    if (isSubstring) {
                        LOGGER.debug("Exact/partial match found: Product: {}, Query: {}, Is Substring: {}", product.getName(), query, isSubstring);
                        return true;
                    }

                    // Calculate Damerau-Levenshtein distance
                    int distance = distanceCalculator.calculateDistance(productName, lowerCaseQuery);
                    double normalizedDistance = (double) distance / Math.max(productName.length(), lowerCaseQuery.length());

                    LOGGER.debug("Fuzzy match details: Product: {}, Query: {}, Distance: {}, Normalized Distance: {}", product.getName(), query, distance, normalizedDistance);

                    // Using normalized distance threshold (e.g., <= 0.3)
                    return normalizedDistance <= normalizedDistanceThreshold;
                })
                // Sort by relevance (lowest normalized distance first)
                .sorted(Comparator.comparingDouble(product -> {
                    String productName = product.getName().toLowerCase();
                    int distance = distanceCalculator.calculateDistance(productName, lowerCaseQuery);
                    return (double) distance / Math.max(productName.length(), lowerCaseQuery.length());
                }))
                .collect(Collectors.toList());

        // Check if any products matched the query
        if (matchedProducts.isEmpty()) {
            LOGGER.error("ATTENTION!!! No matched products found");
        }else{
            LOGGER.info("matched products: "+matchedProducts.size());
        }
        return matchedProducts;

    }

    public boolean loadProductsFromCSV(InputStream inputStream) {
        int noOfProducts = 0;
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            // Read the header line String
            String headerLine = br.readLine();
            if (headerLine == null) {
                return false; // Empty file
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Product product = Product.builder().name(data[0]).description(data[1])
                        .category(data[2]).price(Double.parseDouble(data[3]))
                        .imageUrl(data[4]).build();
                addProduct(product);
                noOfProducts++;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("ATTENTION!!! Product data NOT loaded:: REASON:  "+e.getMessage());
            return false;
        } finally {
            LOGGER.info("No of products loaded successfully is : {}", noOfProducts);
        }
    }

    @PostConstruct
    public void init() {
        // Load the CSV file from the resources folder
        InputStream inputStream = getClass().getResourceAsStream("/" + csvFileName);
        boolean isLoaded = false;
        if (inputStream != null && loadProductsFromCSV(inputStream)) {
            LOGGER.info("Product data loaded successfully");
        } else {
            LOGGER.error("ATTENTION!!! Product data NOT loaded");
        }
    }
}
