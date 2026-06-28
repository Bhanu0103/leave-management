package com.auth_service.controller;

import com.auth_service.dto.AuthResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.UserResponse;
import com.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import com.auth_service.dto.KycSubmissionRequest;
import com.auth_service.dto.UpdateProfileRequest;
import com.auth_service.dto.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserResponse>> getManagers() {
        return ResponseEntity.ok(authService.getManagers());
    }

    @PostMapping("/approve/{userId}")
    public ResponseEntity<String> approveUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.approveUser(userId));
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.blockUser(userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.deleteUser(userId));
    }

    @PostMapping("/unblock/{userId}")
    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.unblockUser(userId));
    }

    @PutMapping("/users/{userId}/assign-manager")
    public ResponseEntity<String> assignManager(@PathVariable Long userId, @RequestParam Long managerId) {
        return ResponseEntity.ok(authService.assignManager(userId, managerId));
    }

    @PostMapping("/users/{id}/kyc")
    public ResponseEntity<?> submitKyc(@PathVariable Long id, @RequestBody KycSubmissionRequest request) {
        return ResponseEntity.ok(authService.submitKyc(id, request));
    }

    @PutMapping("/users/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(id, request));
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(id, request));
    }
}
