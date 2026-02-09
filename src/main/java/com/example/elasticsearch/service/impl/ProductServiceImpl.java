package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.repository.ProductRepository;
import com.example.elasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        log.info("Saving product: {}", product.getName());
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        log.debug("Finding product by id: {}", id);
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        log.debug("Finding all products");
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        log.debug("Finding all products with pagination: {}", pageable);
        return productRepository.findAll(pageable);
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting product by id: {}", id);
        productRepository.deleteById(id);
    }

    @Override
    public void delete(Product product) {
        log.info("Deleting product: {}", product.getName());
        productRepository.delete(product);
    }

    @Override
    public List<Product> findByCategory(String category) {
        log.debug("Finding products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> findByBrand(String brand) {
        log.debug("Finding products by brand: {}", brand);
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> findByCategoryAndBrand(String category, String brand) {
        log.debug("Finding products by category and brand: {}, {}", category, brand);
        return productRepository.findByCategoryAndBrand(category, brand);
    }

    @Override
    public Page<Product> findByActiveTrue(Pageable pageable) {
        log.debug("Finding active products with pagination: {}", pageable);
        return productRepository.findByActiveTrue(pageable);
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Finding products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        log.debug("Finding products by name containing: {}", name);
        return productRepository.findByNameContaining(name);
    }

    @Override
    public List<Product> searchByName(String name) {
        log.debug("Searching products by name: {}", name);
        return productRepository.searchByName(name);
    }

    @Override
    public List<Product> searchByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Searching products by category and price range: {}, {} - {}", category, minPrice, maxPrice);
        return productRepository.searchByCategoryAndPriceRange(category, minPrice, maxPrice);
    }

    @Override
    public long countActiveProducts() {
        return productRepository.countByActiveTrue();
    }

    @Override
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    @Override
    public boolean existsById(String id) {
        return productRepository.existsById(id);
    }
}
