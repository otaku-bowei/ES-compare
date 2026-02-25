package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.repository.ProductRepository;
import com.example.elasticsearch.service.ProductService;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .category("Electronics")
                .brand("TestBrand")
                .price(new BigDecimal("99.99"))
                .stock(100)
                .tags(Arrays.asList("test", "product"))
                .active(true)
                .createdAt(DateUtil.now())
                .updatedAt(DateUtil.now())
                .build();
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.save(testProduct);

        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findById("1");

        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should return empty when product not found by ID")
    void testFindByIdNotFound() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById("999");

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find products by category")
    void testFindByCategory() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategory("Electronics")).thenReturn(products);

        List<Product> result = productService.findByCategory("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    @DisplayName("Should find products by brand")
    void testFindByBrand() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByBrand("TestBrand")).thenReturn(products);

        List<Product> result = productService.findByBrand("TestBrand");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestBrand", result.get(0).getBrand());
        verify(productRepository, times(1)).findByBrand("TestBrand");
    }

    @Test
    @DisplayName("Should find products by category and brand")
    void testFindByCategoryAndBrand() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryAndBrand("Electronics", "TestBrand")).thenReturn(products);

        List<Product> result = productService.findByCategoryAndBrand("Electronics", "TestBrand");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByCategoryAndBrand("Electronics", "TestBrand");
    }

    @Test
    @DisplayName("Should find products by price range")
    void testFindByPriceRange() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByPriceBetween(new BigDecimal("50.00"), new BigDecimal("150.00"))).thenReturn(products);

        List<Product> result = productService.findByPriceRange(new BigDecimal("50.00"), new BigDecimal("150.00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByPriceBetween(new BigDecimal("50.00"), new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Should find products by name containing")
    void testFindByNameContaining() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContaining("Test")).thenReturn(products);

        List<Product> result = productService.findByNameContaining("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByNameContaining("Test");
    }

    @Test
    @DisplayName("Should search products by name")
    void testSearchByName() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByName("Test")).thenReturn(products);

        List<Product> result = productService.searchByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchByName("Test");
    }

    @Test
    @DisplayName("Should count active products")
    void testCountActiveProducts() {
        when(productRepository.countByActiveTrue()).thenReturn(10L);

        long count = productService.countActiveProducts();

        assertEquals(10L, count);
        verify(productRepository, times(1)).countByActiveTrue();
    }

    @Test
    @DisplayName("Should count products by category")
    void testCountByCategory() {
        when(productRepository.countByCategory("Electronics")).thenReturn(5L);

        long count = productService.countByCategory("Electronics");

        assertEquals(5L, count);
        verify(productRepository, times(1)).countByCategory("Electronics");
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void testExistsById() {
        when(productRepository.existsById("1")).thenReturn(true);
        when(productRepository.existsById("999")).thenReturn(false);

        assertTrue(productService.existsById("1"));
        assertFalse(productService.existsById("999"));
        verify(productRepository, times(1)).existsById("1");
        verify(productRepository, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should delete product by ID")
    void testDeleteById() {
        doNothing().when(productRepository).deleteById("1");

        productService.deleteById("1");

        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should find all products with pagination")
    void testFindAllWithPagination() {
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find active products with pagination")
    void testFindByActiveTrueWithPagination() {
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, 1);
        when(productRepository.findByActiveTrue(pageable)).thenReturn(page);

        Page<Product> result = productService.findByActiveTrue(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findByActiveTrue(pageable);
    }

    @Test
    @DisplayName("Should search products by category and price range")
    void testSearchByCategoryAndPriceRange() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.searchByCategoryAndPriceRange("Electronics", new BigDecimal("50.00"), new BigDecimal("150.00")))
                .thenReturn(products);

        List<Product> result = productService.searchByCategoryAndPriceRange("Electronics", new BigDecimal("50.00"), new BigDecimal("150.00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchByCategoryAndPriceRange("Electronics", new BigDecimal("50.00"), new BigDecimal("150.00"));
    }
}
