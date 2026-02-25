package com.example.elasticsearch.controller;

import com.example.elasticsearch.client.ElasticsearchRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/es")
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticsearchRestClient elasticsearchRestClient;

    @GetMapping("/health")
    public ResponseEntity<String> getClusterHealth() {
        return elasticsearchRestClient.getClusterHealth();
    }

    @GetMapping("/indices")
    public ResponseEntity<String> getIndices() {
        return elasticsearchRestClient.getIndices();
    }

    @GetMapping("/mapping/{index}")
    public ResponseEntity<String> getMapping(@PathVariable String index) {
        return elasticsearchRestClient.getMapping(index);
    }

    @PostMapping("/index/{index}")
    public ResponseEntity<String> createIndex(@PathVariable String index, @RequestBody(required = false) String mapping) {
        String mappingJson = mapping != null ? mapping : "{}";
        return elasticsearchRestClient.createIndex(index, mappingJson);
    }

    @DeleteMapping("/index/{index}")
    public ResponseEntity<String> deleteIndex(@PathVariable String index) {
        return elasticsearchRestClient.deleteIndex(index);
    }

    @GetMapping("/doc/{index}/{id}")
    public ResponseEntity<String> getDocument(@PathVariable String index, @PathVariable String id) {
        return elasticsearchRestClient.searchById(index, id);
    }

    @PostMapping("/doc/{index}/{id}")
    public ResponseEntity<String> indexDocument(@PathVariable String index,
                                                 @PathVariable String id,
                                                 @RequestBody String document) {
        return elasticsearchRestClient.indexDocument(index, id, document);
    }

    @DeleteMapping("/doc/{index}/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable String index, @PathVariable String id) {
        return elasticsearchRestClient.deleteDocument(index, id);
    }

    @PostMapping("/doc/{index}/{id}/_update")
    public ResponseEntity<String> updateDocument(@PathVariable String index,
                                                  @PathVariable String id,
                                                  @RequestBody String updateJson) {
        return elasticsearchRestClient.updateDocument(index, id, updateJson);
    }

    @PostMapping("/search/{index}")
    public ResponseEntity<String> search(@PathVariable String index, @RequestBody String query) {
        return elasticsearchRestClient.search(index, query);
    }

    @PostMapping("/aggregate/{index}")
    public ResponseEntity<String> aggregate(@PathVariable String index, @RequestBody String aggQuery) {
        return elasticsearchRestClient.aggregate(index, aggQuery);
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> bulk(@RequestBody String bulkJson) {
        return elasticsearchRestClient.bulkRequest(bulkJson);
    }
}
