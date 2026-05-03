package com.journal.backend.controller;

import com.journal.backend.dto.RegisterRequest;
import com.journal.backend.dto.UserSummaryDTO;
import com.journal.backend.entity.User;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    @GetMapping("/{id}")
    public UserSummaryDTO getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(this::toSummary)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @PostMapping("/register")
    public UserSummaryDTO registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("AUTHOR");
        user.setCreatedAt(LocalDateTime.now());

        return toSummary(userRepository.save(user));
    }

    private UserSummaryDTO toSummary(User user) {
        return new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getRoles()
        );
    }
}
