package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.service.AdvancedSearchService;
import com.example.elasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AdvancedSearchService advancedSearchService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Creating product: {}", product.getName());
        Product savedProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        log.debug("Getting product by id: {}", id);
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Getting all products, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        log.info("Updating product: {}", id);
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Deleting product: {}", id);
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        log.debug("Getting products by category: {}", category);
        List<Product> products = productService.findByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brand) {
        log.debug("Getting products by brand: {}", brand);
        List<Product> products = productService.findByBrand(brand);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        log.debug("Getting products by price range: {} - {}", minPrice, maxPrice);
        List<Product> products = productService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        log.debug("Searching products by name: {}", name);
        List<Product> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/complex")
    public ResponseEntity<List<Product>> complexSearch(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String brand,
                                                        @RequestParam(required = false) BigDecimal minPrice,
                                                        @RequestParam(required = false) BigDecimal maxPrice) {
        log.debug("Performing complex search");
        Map<String, Object> criteria = new HashMap<>();
        if (name != null) criteria.put("name", name);
        if (category != null) criteria.put("category", category);
        if (brand != null) criteria.put("brand", brand);
        if (minPrice != null) criteria.put("minPrice", minPrice);
        if (maxPrice != null) criteria.put("maxPrice", maxPrice);

        List<Product> products = advancedSearchService.complexProductSearch(criteria);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/fuzzy")
    public ResponseEntity<List<Product>> fuzzySearch(@RequestParam String query,
                                                      @RequestParam(defaultValue = "1.0") float fuzziness) {
        log.debug("Performing fuzzy search: {}, fuzziness: {}", query, fuzziness);
        List<Product> products = advancedSearchService.fuzzyProductSearch(query, fuzziness);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/multi-match")
    public ResponseEntity<List<Product>> multiMatchSearch(@RequestParam String[] fields,
                                                           @RequestParam String query) {
        log.debug("Performing multi-match search");
        List<Product> products = advancedSearchService.productMultiMatch(fields, query);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/highlight")
    public ResponseEntity<List<Product>> highlightSearch(@RequestParam String[] fields,
                                                          @RequestParam String query) {
        log.debug("Performing highlight search");
        List<Product> products = advancedSearchService.productHighlightSearch(fields, query);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/aggregations")
    public ResponseEntity<Map<String, Long>> getAggregations() {
        log.debug("Getting product aggregations");
        Map<String, Long> stats = advancedSearchService.productAggregationStatsByCategory();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveProducts() {
        log.debug("Counting active products");
        long count = productService.countActiveProducts();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<Long> countByCategory(@PathVariable String category) {
        log.debug("Counting products by category: {}", category);
        long count = productService.countByCategory(category);
        return ResponseEntity.ok(count);
    }
}
