package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.User;
import com.example.elasticsearch.repository.UserRepository;
import com.example.elasticsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        log.info("Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        return (List<User>) userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting user by id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void delete(User user) {
        log.info("Deleting user: {}", user.getUsername());
        userRepository.delete(user);
    }

    @Override
    public List<User> findByUsername(String username) {
        log.debug("Finding users by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByEmail(String email) {
        log.debug("Finding users by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByActiveTrue() {
        log.debug("Finding active users");
        return userRepository.findByActiveTrue();
    }

    @Override
    public Page<User> findByActiveTrue(Pageable pageable) {
        log.debug("Finding active users with pagination: {}", pageable);
        return userRepository.findByActiveTrue(pageable);
    }

    @Override
    public List<User> findByAgeRange(Integer minAge, Integer maxAge) {
        log.debug("Finding users by age range: {} - {}", minAge, maxAge);
        return userRepository.findByAgeBetween(minAge, maxAge);
    }

    @Override
    public List<User> findByFullNameContaining(String fullName) {
        log.debug("Finding users by fullName containing: {}", fullName);
        return userRepository.findByFullNameContaining(fullName);
    }

    @Override
    public List<User> searchByFullName(String fullName) {
        log.debug("Searching users by fullName: {}", fullName);
        return userRepository.searchByFullName(fullName);
    }

    @Override
    public List<User> findActiveUsersByAgeRange(Integer minAge, Integer maxAge) {
        log.debug("Finding active users by age range: {} - {}", minAge, maxAge);
        return userRepository.findActiveUsersByAgeRange(minAge, maxAge);
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    @Override
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }
}
