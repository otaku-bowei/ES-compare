package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    void deleteById(String id);

    void delete(Product product);

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByCategoryAndBrand(String category, String brand);

    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByNameContaining(String name);

    List<Product> searchByName(String name);

    List<Product> searchByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice);

    long countActiveProducts();

    long countByCategory(String category);

    boolean existsById(String id);
}
