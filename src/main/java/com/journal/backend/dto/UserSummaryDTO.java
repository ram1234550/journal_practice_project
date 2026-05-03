package com.journal.backend.dto;

import java.util.List;

public class UserSummaryDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private List<String> roles;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Long id, String name, String email, String role, List<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
