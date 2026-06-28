package com.auth_service.dto;

import com.auth_service.model.Role;

public class UserResponse {
    private Long id;
    private String username;
    private Role role;
    private Long managerId;
    private String email;
    private boolean approved;
    private String status;
    private Long hrId;
    private String panCard;
    private String certificatesLink;
    private String phoneNumber;
    private String address;
    private String bio;

    public UserResponse() {}

    public UserResponse(Long id, String username, Role role, Long managerId, String email, boolean approved, String status, Long hrId, String panCard, String certificatesLink, String phoneNumber, String address, String bio) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.managerId = managerId;
        this.email = email;
        this.approved = approved;
        this.status = status;
        this.hrId = hrId;
        this.panCard = panCard;
        this.certificatesLink = certificatesLink;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.bio = bio;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getHrId() { return hrId; }
    public void setHrId(Long hrId) { this.hrId = hrId; }

    public String getPanCard() { return panCard; }
    public void setPanCard(String panCard) { this.panCard = panCard; }

    public String getCertificatesLink() { return certificatesLink; }
    public void setCertificatesLink(String certificatesLink) { this.certificatesLink = certificatesLink; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
