package com.rpms.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ProductcatalogApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProductcatalogApplication.class, args);
	}

}
