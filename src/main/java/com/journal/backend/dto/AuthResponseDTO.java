package com.journal.backend.dto;

public class AuthResponseDTO {

    private String token;
    private String role;
    private String name;
    private String id;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String role, String name, String id) {
        this.token = token;
        this.role = role;
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
