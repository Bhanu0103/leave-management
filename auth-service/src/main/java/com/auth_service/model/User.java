package com.auth_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Long managerId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private String status = "ACTIVE";

    private Long hrId;

    private String panCard;

    private String certificatesLink;

    private String phoneNumber;
    private String address;
    private String bio;

    // Constructors
    public User() {}

    public User(String username, String password, Role role, Long hrId, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.hrId = hrId;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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
