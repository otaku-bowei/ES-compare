package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByCategoryAndBrand(String category, String brand);

    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByNameContaining(String name);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
    List<Product> searchByName(String name);

    @Query("{\"bool\": {\"filter\": [{\"term\": {\"category\": \"?0\"}}, {\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}]}}")
    List<Product> searchByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice);

    long countByActiveTrue();

    long countByCategory(String category);
}
