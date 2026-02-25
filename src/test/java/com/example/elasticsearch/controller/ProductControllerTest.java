package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.service.AdvancedSearchService;
import com.example.elasticsearch.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private AdvancedSearchService advancedSearchService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

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
    @DisplayName("Should create product successfully")
    void testCreateProduct() throws Exception {
        when(productService.save(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category").value("Electronics"));

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should get product by ID")
    void testGetProduct() throws Exception {
        when(productService.findById("1")).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void testGetProductNotFound() throws Exception {
        when(productService.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should get all products with pagination")
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, 1);
        when(productService.findAll(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("1"));

        verify(productService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() throws Exception {
        when(productService.existsById("1")).thenReturn(true);
        when(productService.save(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(productService, times(1)).existsById("1");
        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void testUpdateProductNotFound() throws Exception {
        when(productService.existsById("999")).thenReturn(false);

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).existsById("999");
        verify(productService, times(0)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct() throws Exception {
        when(productService.existsById("1")).thenReturn(true);
        doNothing().when(productService).deleteById("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).existsById("1");
        verify(productService, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent product")
    void testDeleteProductNotFound() throws Exception {
        when(productService.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).existsById("999");
        verify(productService, times(0)).deleteById("999");
    }

    @Test
    @DisplayName("Should get products by category")
    void testGetProductsByCategory() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findByCategory("Electronics")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(productService, times(1)).findByCategory("Electronics");
    }

    @Test
    @DisplayName("Should get products by brand")
    void testGetProductsByBrand() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findByBrand("TestBrand")).thenReturn(products);

        mockMvc.perform(get("/api/products/brand/TestBrand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].brand").value("TestBrand"));

        verify(productService, times(1)).findByBrand("TestBrand");
    }

    @Test
    @DisplayName("Should get products by price range")
    void testGetProductsByPriceRange() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.findByPriceRange(new BigDecimal("50.00"), new BigDecimal("150.00"))).thenReturn(products);

        mockMvc.perform(get("/api/products/price-range")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "150.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").value(99.99));

        verify(productService, times(1)).findByPriceRange(new BigDecimal("50.00"), new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Should search products by name")
    void testSearchByName() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.searchByName("Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search/name")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).searchByName("Test");
    }

    @Test
    @DisplayName("Should perform fuzzy search")
    void testFuzzySearch() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(advancedSearchService.fuzzyProductSearch("Test", 1.0f)).thenReturn(products);

        mockMvc.perform(get("/api/products/search/fuzzy")
                        .param("query", "Test")
                        .param("fuzziness", "1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(advancedSearchService, times(1)).fuzzyProductSearch("Test", 1.0f);
    }

    @Test
    @DisplayName("Should perform multi-match search")
    void testMultiMatchSearch() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(advancedSearchService.productMultiMatch(new String[]{"name", "description"}, "Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search/multi-match")
                        .param("fields", "name", "description")
                        .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(advancedSearchService, times(1)).productMultiMatch(new String[]{"name", "description"}, "Test");
    }

    @Test
    @DisplayName("Should perform highlight search")
    void testHighlightSearch() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(advancedSearchService.productHighlightSearch(new String[]{"name", "description"}, "Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search/highlight")
                        .param("fields", "name", "description")
                        .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(advancedSearchService, times(1)).productHighlightSearch(new String[]{"name", "description"}, "Test");
    }

    @Test
    @DisplayName("Should get aggregations")
    void testGetAggregations() throws Exception {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("Electronics", 10L);
        when(advancedSearchService.productAggregationStatsByCategory()).thenReturn(stats);

        mockMvc.perform(get("/api/products/aggregations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Electronics").value(10));

        verify(advancedSearchService, times(1)).productAggregationStatsByCategory();
    }

    @Test
    @DisplayName("Should count active products")
    void testCountActiveProducts() throws Exception {
        when(productService.countActiveProducts()).thenReturn(100L);

        mockMvc.perform(get("/api/products/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(productService, times(1)).countActiveProducts();
    }

    @Test
    @DisplayName("Should count products by category")
    void testCountByCategory() throws Exception {
        when(productService.countByCategory("Electronics")).thenReturn(5L);

        mockMvc.perform(get("/api/products/count/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(productService, times(1)).countByCategory("Electronics");
    }
}
