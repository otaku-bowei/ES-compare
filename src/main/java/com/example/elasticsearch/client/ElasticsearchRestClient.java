package com.example.elasticsearch.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class ElasticsearchRestClient {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    private final RestTemplate restTemplate;

    public ElasticsearchRestClient() {
        this.restTemplate = new RestTemplate();
    }

    private String getBaseUrl() {
        return elasticsearchUri;
    }

    public ResponseEntity<String> search(String index, String queryJson) {
        String url = getBaseUrl() + "/" + index + "/_search";
        log.info("Executing search request to: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(queryJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> searchById(String index, String id) {
        String url = getBaseUrl() + "/" + index + "/_doc/" + id;
        log.info("Fetching document by id: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> indexDocument(String index, String id, String documentJson) {
        String url = getBaseUrl() + "/" + index + "/_doc/" + id;
        log.info("Indexing document to: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(documentJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> deleteDocument(String index, String id) {
        String url = getBaseUrl() + "/" + index + "/_doc/" + id;
        log.info("Deleting document: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    public ResponseEntity<String> updateDocument(String index, String id, String updateJson) {
        String url = getBaseUrl() + "/" + index + "/_doc/" + id + "/_update";
        log.info("Updating document: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(updateJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> createIndex(String index, String mappingJson) {
        String url = getBaseUrl() + "/" + index;
        log.info("Creating index: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(mappingJson, headers);

        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    public ResponseEntity<String> deleteIndex(String index) {
        String url = getBaseUrl() + "/" + index;
        log.info("Deleting index: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    public ResponseEntity<String> getClusterHealth() {
        String url = getBaseUrl() + "/_cluster/health";
        log.info("Getting cluster health: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> getIndices() {
        String url = getBaseUrl() + "/_cat/indices?v";
        log.info("Getting indices: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> getMapping(String index) {
        String url = getBaseUrl() + "/" + index + "/_mapping";
        log.info("Getting mapping for index: {}", url);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<String> bulkRequest(String bulkJson) {
        String url = getBaseUrl() + "/_bulk";
        log.info("Executing bulk request: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bulkJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<String> aggregate(String index, String aggJson) {
        String url = getBaseUrl() + "/" + index + "/_search";
        log.info("Executing aggregation: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(aggJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
