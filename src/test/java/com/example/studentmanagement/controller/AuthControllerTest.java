package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.LoginRequest;
import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Mocked constants for tests
    private final String TEST_USERNAME = "testUser";
    private final String TEST_PASSWORD = "testPassword";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccessful() {
        // Arrange: Creating a User object
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD)); // Encrypt password for testing

        // Mocking UserService behavior
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(true);

        // Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        // Act: Perform login
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        // Assert: Check response status and body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("token")); // Verify the response contains a JWT token
    }

    @Test
    public void testLoginFailure_UserNotFound() {
        // Arrange: Mocking the behavior for the case where the user doesn't exist
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(null);

        // Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        // Act: Perform login
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        // Assert: Verify unauthorized response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("message"));
    }

    @Test
    public void testLoginFailure_InvalidPassword() {
        // Arrange: Create a user and mock the validation
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode("differentPassword")); // Store a different password

        when(userService.findByUsername(TEST_USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(false); // Password does not match

        // Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        // Act: Attempt login
        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        // Assert: Verify unauthorized response
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("message"));
    }
}
