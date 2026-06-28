package com.auth_service.service;

import com.auth_service.dto.AuthResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;
import com.auth_service.dto.KycSubmissionRequest;
import com.auth_service.dto.UserResponse;
import com.auth_service.dto.UpdateProfileRequest;
import com.auth_service.dto.ChangePasswordRequest;
import com.auth_service.model.Role;
import com.auth_service.model.User;
import com.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import com.auth_service.exception.ResourceNotFoundException;
import com.auth_service.exception.BadRequestException;
import com.auth_service.exception.UnauthorizedException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (request.getRole() == Role.EMPLOYEE || request.getRole() == Role.MANAGER) {
            if (request.getHrId() == null) {
                throw new BadRequestException("HR ID is mandatory for employees and managers");
            }
            User hr = userRepository.findById(request.getHrId())
                    .orElseThrow(() -> new ResourceNotFoundException("HR not found with ID " + request.getHrId()));
            if (hr.getRole() != Role.HR) {
                throw new BadRequestException("The specified HR ID does not belong to a user with the HR role");
            }
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole(),
                request.getHrId(),
                request.getEmail()
        );
        if (request.getRole() == Role.EMPLOYEE) {
            user.setApproved(false);
        } else {
            user.setApproved(true);
        }

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if ("BLOCKED".equals(user.getStatus())) {
            throw new UnauthorizedException("User account is blocked");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getManagerId()
        );

        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + id));
        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getManagers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MANAGER)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getManagerId(),
                user.getEmail(),
                user.isApproved(),
                user.getStatus(),
                user.getHrId(),
                user.getPanCard(),
                user.getCertificatesLink(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getBio()
        );
    }

    @Transactional
    public String approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        user.setApproved(true);
        userRepository.save(user);
        return "approved";
    }

    @Transactional
    public String blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        user.setStatus("BLOCKED");
        userRepository.save(user);
        return "blocked";
    }

    @Transactional
    public String unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        user.setStatus("ACTIVE");
        userRepository.save(user);
        return "unblocked";
    }

    @Transactional
    public String assignManager(Long userId, Long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID " + managerId));
        if (manager.getRole() != Role.MANAGER) {
            throw new BadRequestException("Assigned user must be a MANAGER");
        }
        user.setManagerId(managerId);
        userRepository.save(user);
        return "assigned";
    }

    @Transactional
    public String submitKyc(Long userId, KycSubmissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        user.setPanCard(request.getPanCard());
        user.setCertificatesLink(request.getCertificatesLink());
        userRepository.save(user);
        return "KYC submitted";
    }

    @Transactional
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        userRepository.delete(user);
        return "deleted";
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setBio(request.getBio());
        userRepository.save(user);
        return convertToResponse(user);
    }

    @Transactional
    public String changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + userId));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }
}
