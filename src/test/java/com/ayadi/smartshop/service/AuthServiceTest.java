package com.ayadi.smartshop.service;

import com.ayadi.smartshop.dto.request.LoginRequest;
import com.ayadi.smartshop.dto.response.UserResponse;
import com.ayadi.smartshop.entity.User;
import com.ayadi.smartshop.enums.UserRole;
import com.ayadi.smartshop.exception.UnauthorizedException;
import com.ayadi.smartshop.mapper.UserMapper;
import com.ayadi.smartshop.repository.UserRepository;
import com.ayadi.smartshop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("admin")
                .password("admin123")
                .role(UserRole.ADMIN)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("admin");
        userResponse.setRole(UserRole.ADMIN);
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // Act
        UserResponse result = authService.login(loginRequest, session);

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(session).setAttribute("user", testUser);
        verify(userRepository).findByUsername("admin");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void login_UserNotFound_ThrowsUnauthorizedException() {
        // Arrange
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(loginRequest, session)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("admin");
        verify(session, never()).setAttribute(any(), any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void login_WrongPassword_ThrowsUnauthorizedException() {
        // Arrange
        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(loginRequest, session)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("admin");
        verify(session, never()).setAttribute(any(), any());
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void logout_Success() {
        // Act
        authService.logout(session);

        // Assert
        verify(session).invalidate();
    }

    @Test
    void getCurrentUser_Success() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(testUser);

        // Act
        User result = authService.getCurrentUser(session);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(session).getAttribute("user");
    }

    @Test
    void getCurrentUser_NoUserInSession_ThrowsUnauthorizedException() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.getCurrentUser(session)
        );

        assertEquals("Not authenticated", exception.getMessage());
        verify(session).getAttribute("user");
    }

    @Test
    void getCurrentUserResponse_Success() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        // Act
        UserResponse result = authService.getCurrentUserResponse(session);

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(session).getAttribute("user");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void getCurrentUserResponse_NoUserInSession_ThrowsUnauthorizedException() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(null);

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.getCurrentUserResponse(session)
        );

        assertEquals("Not authenticated", exception.getMessage());
        verify(session).getAttribute("user");
        verify(userMapper, never()).toResponse(any());
    }
}
