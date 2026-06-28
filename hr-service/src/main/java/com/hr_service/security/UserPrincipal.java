package com.hr_service.security;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final Long id;
    private final String username;
    private final String role;
    private final Long managerId;

    public UserPrincipal(Long id, String username, String role, Long managerId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.managerId = managerId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Long getManagerId() {
        return managerId;
    }
}
