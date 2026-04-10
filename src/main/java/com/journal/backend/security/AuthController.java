package com.journal.backend.controller;

import com.journal.backend.entity.User;
import com.journal.backend.repository.UserRepository;
import com.journal.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST /api/auth/login
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        // Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем пароль — сравниваем с хешем в БД
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        // Генерируем токен и возвращаем
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
                "token", token,
                "role", user.getRole(),
                "name", user.getName(),
                "id", String.valueOf(user.getId())
        );
    }
}