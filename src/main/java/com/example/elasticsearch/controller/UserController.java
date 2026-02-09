package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.User;
import com.example.elasticsearch.service.AdvancedSearchService;
import com.example.elasticsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdvancedSearchService advancedSearchService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Creating user: {}", user.getUsername());
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        log.debug("Getting user by id: {}", id);
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Getting all users, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        log.info("Updating user: {}", id);
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Deleting user: {}", id);
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        log.debug("Getting users by username: {}", username);
        List<User> users = userService.findByUsername(username);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<User>> getUsersByEmail(@PathVariable String email) {
        log.debug("Getting users by email: {}", email);
        List<User> users = userService.findByEmail(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/age-range")
    public ResponseEntity<List<User>> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        log.debug("Getting users by age range: {} - {}", minAge, maxAge);
        List<User> users = userService.findByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/fullName")
    public ResponseEntity<List<User>> searchByFullName(@RequestParam String fullName) {
        log.debug("Searching users by fullName: {}", fullName);
        List<User> users = userService.searchByFullName(fullName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search/complex")
    public ResponseEntity<List<User>> complexSearch(@RequestParam(required = false) String fullName,
                                                      @RequestParam(required = false) String email,
                                                      @RequestParam(required = false) Integer minAge,
                                                      @RequestParam(required = false) Integer maxAge,
                                                      @RequestParam(required = false) Boolean active) {
        log.debug("Performing complex user search");
        Map<String, Object> criteria = new HashMap<>();
        if (fullName != null) criteria.put("fullName", fullName);
        if (email != null) criteria.put("email", email);
        if (minAge != null) criteria.put("minAge", minAge);
        if (maxAge != null) criteria.put("maxAge", maxAge);
        if (active != null) criteria.put("active", active);

        List<User> users = advancedSearchService.complexUserSearch(criteria);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveUsers() {
        log.debug("Counting active users");
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
}
