package com.example.elasticsearch.controller;

import com.example.elasticsearch.document.User;
import com.example.elasticsearch.service.AdvancedSearchService;
import com.example.elasticsearch.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AdvancedSearchService advancedSearchService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUser = User.builder()
                .id("1")
                .username("testuser")
                .fullName("Test User")
                .email("test@example.com")
                .phone("1234567890")
                .address("123 Test Street")
                .age(25)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() throws Exception {
        when(userService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID")
    void testGetUser() throws Exception {
        when(userService.findById("1")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).findById("1");
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    void testGetUserNotFound() throws Exception {
        when(userService.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById("999");
    }

    @Test
    @DisplayName("Should get all users with pagination")
    void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(users, pageable, 1);
        when(userService.findAll(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("1"));

        verify(userService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() throws Exception {
        when(userService.existsById("1")).thenReturn(true);
        when(userService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(userService, times(1)).existsById("1");
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void testUpdateUserNotFound() throws Exception {
        when(userService.existsById("999")).thenReturn(false);

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).existsById("999");
        verify(userService, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() throws Exception {
        when(userService.existsById("1")).thenReturn(true);
        doNothing().when(userService).deleteById("1");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).existsById("1");
        verify(userService, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    void testDeleteUserNotFound() throws Exception {
        when(userService.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).existsById("999");
        verify(userService, times(0)).deleteById("999");
    }

    @Test
    @DisplayName("Should get users by username")
    void testGetUsersByUsername() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findByUsername("testuser")).thenReturn(users);

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"));

        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should get users by email")
    void testGetUsersByEmail() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findByEmail("test@example.com")).thenReturn(users);

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(userService, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should get users by age range")
    void testGetUsersByAgeRange() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.findByAgeRange(18, 30)).thenReturn(users);

        mockMvc.perform(get("/api/users/age-range")
                        .param("minAge", "18")
                        .param("maxAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].age").value(25));

        verify(userService, times(1)).findByAgeRange(18, 30);
    }

    @Test
    @DisplayName("Should search users by fullName")
    void testSearchByFullName() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.searchByFullName("Test")).thenReturn(users);

        mockMvc.perform(get("/api/users/search/fullName")
                        .param("fullName", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fullName").value("Test User"));

        verify(userService, times(1)).searchByFullName("Test");
    }

    @Test
    @DisplayName("Should perform complex user search")
    void testComplexSearch() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(advancedSearchService.complexUserSearch(any())).thenReturn(users);

        mockMvc.perform(get("/api/users/search/complex")
                        .param("fullName", "Test")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(advancedSearchService, times(1)).complexUserSearch(any());
    }

    @Test
    @DisplayName("Should count active users")
    void testCountActiveUsers() throws Exception {
        when(userService.countActiveUsers()).thenReturn(100L);

        mockMvc.perform(get("/api/users/count/active"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(userService, times(1)).countActiveUsers();
    }
}
