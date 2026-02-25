package com.example.elasticsearch.service.impl;

import com.example.elasticsearch.document.User;
import com.example.elasticsearch.repository.UserRepository;
import com.example.elasticsearch.service.UserService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("1")
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .phone("1234567890")
                .address("123 Test Street")
                .age(25)
                .active(true)
                .createdAt(DateUtil.now())
                .updatedAt(DateUtil.now())
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.save(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById("1");

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testFindByIdNotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.findById("999");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find users by username")
    void testFindByUsername() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByUsername("testuser")).thenReturn(users);

        List<User> result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should find users by email")
    void testFindByEmail() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByEmail("test@example.com")).thenReturn(users);

        List<User> result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should find active users")
    void testFindByActiveTrue() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByActiveTrue()).thenReturn(users);

        List<User> result = userService.findByActiveTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Should find users by age range")
    void testFindByAgeRange() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByAgeBetween(18, 30)).thenReturn(users);

        List<User> result = userService.findByAgeRange(18, 30);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByAgeBetween(18, 30);
    }

    @Test
    @DisplayName("Should find users by fullName containing")
    void testFindByFullNameContaining() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByFullNameContaining("Test")).thenReturn(users);

        List<User> result = userService.findByFullNameContaining("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByFullNameContaining("Test");
    }

    @Test
    @DisplayName("Should search users by fullName")
    void testSearchByFullName() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.searchByFullName("Test")).thenReturn(users);

        List<User> result = userService.searchByFullName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).searchByFullName("Test");
    }

    @Test
    @DisplayName("Should find active users by age range")
    void testFindActiveUsersByAgeRange() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findActiveUsersByAgeRange(18, 30)).thenReturn(users);

        List<User> result = userService.findActiveUsersByAgeRange(18, 30);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findActiveUsersByAgeRange(18, 30);
    }

    @Test
    @DisplayName("Should count active users")
    void testCountActiveUsers() {
        when(userRepository.countByActiveTrue()).thenReturn(100L);

        long count = userService.countActiveUsers();

        assertEquals(100L, count);
        verify(userRepository, times(1)).countByActiveTrue();
    }

    @Test
    @DisplayName("Should check if user exists by ID")
    void testExistsById() {
        when(userRepository.existsById("1")).thenReturn(true);
        when(userRepository.existsById("999")).thenReturn(false);

        assertTrue(userService.existsById("1"));
        assertFalse(userService.existsById("999"));
        verify(userRepository, times(1)).existsById("1");
        verify(userRepository, times(1)).existsById("999");
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteById() {
        doNothing().when(userRepository).deleteById("1");

        userService.deleteById("1");

        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should find all users with pagination")
    void testFindAllWithPagination() {
        List<User> users = Arrays.asList(testUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find active users with pagination")
    void testFindByActiveTrueWithPagination() {
        List<User> users = Arrays.asList(testUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findByActiveTrue(pageable)).thenReturn(page);

        Page<User> result = userService.findByActiveTrue(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository, times(1)).findByActiveTrue(pageable);
    }
}
