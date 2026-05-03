package com.journal.backend.dto;

import java.util.List;

public class AuthResponseDTO {

    private String token;
    private String role;
    private List<String> roles;
    private String name;
    private String id;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String role, List<String> roles, String name, String id) {
        this.token = token;
        this.role = role;
        this.roles = roles;
        this.name = name;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
