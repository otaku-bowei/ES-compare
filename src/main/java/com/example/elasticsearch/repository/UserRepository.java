package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, String> {

    List<User> findByUsername(String username);

    List<User> findByEmail(String email);

    List<User> findByActiveTrue();

    Page<User> findByActiveTrue(Pageable pageable);

    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    List<User> findByFullNameContaining(String fullName);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"fullName\": \"?0\"}}]}}")
    List<User> searchByFullName(String fullName);

    @Query("{\"bool\": {\"filter\": [{\"term\": {\"active\": true}}, {\"range\": {\"age\": {\"gte\": ?0, \"lte\": ?1}}}]}}")
    List<User> findActiveUsersByAgeRange(Integer minAge, Integer maxAge);

    long countByActiveTrue();
}
