package com.auth_service.service;

import com.auth_service.dto.AuthResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserResponse;
import com.auth_service.exception.BadRequestException;
import com.auth_service.exception.UnauthorizedException;
import com.auth_service.model.Role;
import com.auth_service.model.User;
import com.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    void register_ShouldSaveUser_WhenValid() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com");
        req.setUsername("testuser");
        req.setPassword("password");
        req.setRole(Role.HR);

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse res = authService.register(req);
        assertNotNull(res);
        assertEquals("testuser", res.getUsername());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@test.com");
        req.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        user.setRole(Role.HR);
        user.setApproved(true);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken(anyLong(), any(), any(), any())).thenReturn("jwt-token");

        AuthResponse res = authService.login(req);
        assertEquals("jwt-token", res.getToken());
        assertEquals(1L, res.getUserId());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        LoginRequest req = new LoginRequest();
        req.setEmail("user@test.com");
        req.setPassword("wrong");

        User user = new User();
        user.setPassword("encoded");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
    }
}
