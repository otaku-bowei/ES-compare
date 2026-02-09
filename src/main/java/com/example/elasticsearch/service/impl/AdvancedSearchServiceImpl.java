package com.example.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.example.elasticsearch.document.Product;
import com.example.elasticsearch.document.User;
import com.example.elasticsearch.repository.ProductRepository;
import com.example.elasticsearch.repository.UserRepository;
import com.example.elasticsearch.service.AdvancedSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvancedSearchServiceImpl implements AdvancedSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Product> complexProductSearch(Map<String, Object> searchCriteria) {
        log.debug("Performing complex product search with criteria: {}", searchCriteria);

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (searchCriteria.containsKey("name")) {
            boolQueryBuilder.must(Query.of(q -> q.match(m -> m.field("name").query((String) searchCriteria.get("name")))));
        }

        if (searchCriteria.containsKey("category")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("category").value((String) searchCriteria.get("category")))));
        }

        if (searchCriteria.containsKey("brand")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("brand").value((String) searchCriteria.get("brand")))));
        }

        if (searchCriteria.containsKey("minPrice")) {
            BigDecimal minPrice = new BigDecimal(searchCriteria.get("minPrice").toString());
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("price").gte(JsonData.of(minPrice)))));
        }

        if (searchCriteria.containsKey("maxPrice")) {
            BigDecimal maxPrice = new BigDecimal(searchCriteria.get("maxPrice").toString());
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("price").lte(JsonData.of(maxPrice)))));
        }

        if (searchCriteria.containsKey("active")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("active").value((Boolean) searchCriteria.get("active")))));
        }

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getcollect(Collectors.toListContent)
                .());
    }

    @Override
    public Page<Product> complexProductSearchWithPagination(Map<String, Object> searchCriteria, Pageable pageable) {
        log.debug("Performing complex product search with pagination: {}", pageable);

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (searchCriteria.containsKey("name")) {
            boolQueryBuilder.must(Query.of(q -> q.match(m -> m.field("name").query((String) searchCriteria.get("name")))));
        }

        if (searchCriteria.containsKey("category")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("category").value((String) searchCriteria.get("category")))));
        }

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .withPageable(pageable)
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return new PageImpl<>(searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
    }

    @Override
    public List<User> complexUserSearch(Map<String, Object> searchCriteria) {
        log.debug("Performing complex user search with criteria: {}", searchCriteria);

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (searchCriteria.containsKey("fullName")) {
            boolQueryBuilder.must(Query.of(q -> q.match(m -> m.field("fullName").query((String) searchCriteria.get("fullName")))));
        }

        if (searchCriteria.containsKey("email")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("email").value((String) searchCriteria.get("email")))));
        }

        if (searchCriteria.containsKey("minAge")) {
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("age").gte(JsonData.of(searchCriteria.get("minAge")))));
        }

        if (searchCriteria.containsKey("maxAge")) {
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("age").lte(JsonData.of(searchCriteria.get("maxAge")))));
        }

        if (searchCriteria.containsKey("active")) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("active").value((Boolean) searchCriteria.get("active")))));
        }

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .build();

        SearchHits<User> searchHits = elasticsearchOperations.search(searchQuery, User.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public Page<User> complexUserSearchWithPagination(Map<String, Object> searchCriteria, Pageable pageable) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (searchCriteria.containsKey("fullName")) {
            boolQueryBuilder.must(Query.of(q -> q.match(m -> m.field("fullName").query((String) searchCriteria.get("fullName")))));
        }

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .withPageable(pageable)
                .build();

        SearchHits<User> searchHits = elasticsearchOperations.search(searchQuery, User.class);
        return new PageImpl<>(searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList()), pageable, searchHits.getTotalHits());
    }

    @Override
    public List<Product> fuzzyProductSearch(String query, float fuzziness) {
        log.debug("Performing fuzzy product search: {}, fuzziness: {}", query, fuzziness);

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q
                        .fuzzy(f -> f
                                .field("name")
                                .value(query)
                                .fuzziness(String.valueOf(fuzziness))
                        )
                ))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> productAggregationByCategory() {
        log.debug("Performing product aggregation by category");

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.matchAll(m -> m)))
                .withAggregation("category_agg", Aggregation.of(a -> a
                        .terms(t -> t.field("category").size(100))
                ))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        if (aggregations != null) {
            List<StringTermsBucket> buckets = aggregations.aggregations().get(0).aggregation().getAggregate().sterms().buckets().array();
            return buckets.stream()
                    .map(bucket -> {
                        Product product = new Product();
                        product.setCategory(bucket.key().stringValue());
                        return product;
                    })
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public Map<String, Long> productAggregationStatsByCategory() {
        log.debug("Performing product aggregation stats by category");

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.matchAll(m -> m)))
                .withAggregation("category_count", Aggregation.of(a -> a
                        .terms(t -> t.field("category").size(100))
                ))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        Map<String, Long> stats = new HashMap<>();

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        if (aggregations != null) {
            List<StringTermsBucket> buckets = aggregations.aggregations().get(0).aggregation().getAggregate().sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                stats.put(bucket.key().stringValue(), bucket.docCount());
            }
        }

        return stats;
    }

    @Override
    public List<Product> productBooleanQuery(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Performing boolean query: name={}, category={}, price={}-{}", name, category, minPrice, maxPrice);

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (name != null && !name.isEmpty()) {
            boolQueryBuilder.must(Query.of(q -> q.match(m -> m.field("name").query(name))));
        }

        if (category != null && !category.isEmpty()) {
            boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("category").value(category))));
        }

        if (minPrice != null) {
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("price").gte(JsonData.of(minPrice)))));
        }

        if (maxPrice != null) {
            boolQueryBuilder.filter(Query.of(q -> q.range(r -> r.field("price").lte(JsonData.of(maxPrice)))));
        }

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> productPhraseMatch(String field, String phrase) {
        log.debug("Performing phrase match: field={}, phrase={}", field, phrase);

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q
                        .matchPhrase(mp -> mp
                                .field(field)
                                .query(phrase)
                        )
                ))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> productMultiMatch(String[] fields, String query) {
        log.debug("Performing multi-match: fields={}, query={}", fields, query);

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q
                        .multiMatch(mm -> mm
                                .fields(java.util.Arrays.stream(fields).collect(Collectors.joining(" ")))
                                .query(query)
                        )
                ))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> productHighlightSearch(String[] fields, String query) {
        log.debug("Performing highlight search: fields={}, query={}", fields, query);

        List<HighlightField> highlightFields = java.util.Arrays.stream(fields)
                .map(HighlightField::new)
                .collect(Collectors.toList());

        HighlightParameters highlightParams = HighlightParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .withNumberOfFragments(3)
                .withFragmentSize(150)
                .build();

        Highlight highlight = new Highlight(highlightParams, highlightFields);

        NativeQuery searchQuery = NativeQueryBuilder()
                .withQuery(Query.of(q -> q
                        .multiMatch(mm -> mm
                                .fields(java.util.Arrays.stream(fields).collect(Collectors.joining(" ")))
                                .query(query)
                        )
                ))
                .withHighlightQuery(new HighlightQuery(highlight, Product.class))
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    private static class PageImpl<T> extends org.springframework.data.domain.PageImpl<T> {
        public PageImpl(List<T> content, org.springframework.data.domain.Pageable pageable, long total) {
            super(content, pageable, total);
        }
    }
}
