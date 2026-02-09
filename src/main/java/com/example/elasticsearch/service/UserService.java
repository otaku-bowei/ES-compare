package com.example.elasticsearch.service;

import com.example.elasticsearch.document.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findById(String id);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    void deleteById(String id);

    void delete(User user);

    List<User> findByUsername(String username);

    List<User> findByEmail(String email);

    List<User> findByActiveTrue();

    Page<User> findByActiveTrue(Pageable pageable);

    List<User> findByAgeRange(Integer minAge, Integer maxAge);

    List<User> findByFullNameContaining(String fullName);

    List<User> searchByFullName(String fullName);

    List<User> findActiveUsersByAgeRange(Integer minAge, Integer maxAge);

    long countActiveUsers();

    boolean existsById(String id);
}
