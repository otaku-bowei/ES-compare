package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.document.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdvancedSearchService {

    List<Product> complexProductSearch(Map<String, Object> searchCriteria);

    Page<Product> complexProductSearchWithPagination(Map<String, Object> searchCriteria, Pageable pageable);

    List<User> complexUserSearch(Map<String, Object> searchCriteria);

    Page<User> complexUserSearchWithPagination(Map<String, Object> searchCriteria, Pageable pageable);

    List<Product> fuzzyProductSearch(String query, float fuzziness);

    List<Product> productAggregationByCategory();

    Map<String, Long> productAggregationStatsByCategory();

    List<Product> productBooleanQuery(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> productPhraseMatch(String field, String phrase);

    List<Product> productMultiMatch(String[] fields, String query);

    List<Product> productHighlightSearch(String[] fields, String query);
}
