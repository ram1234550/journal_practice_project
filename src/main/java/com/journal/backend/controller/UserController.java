package com.journal.backend.controller;

import com.journal.backend.entity.User;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@RestController           // говорит Spring: этот класс отвечает на HTTP-запросы
@RequestMapping("/api/users")  // все методы этого класса начинаются с /api/users
@CrossOrigin              // разрешает запросы с фронтенда (другой порт)
public class UserController {

    @Autowired            // Spring сам подставит нужный объект
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET /api/users — получить всех пользователей
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET /api/users/1 — получить пользователя по id
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    // POST /api/users/register — зарегистрировать нового пользователя
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        user.setCreatedAt(LocalDateTime.now());

        // Если роль не указана — ставим AUTHOR по умолчанию
        // Если роль пришла в запросе (REVIEWER, ADMIN) — оставляем её
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("AUTHOR");
        }

        // Хешируем пароль перед сохранением в БД
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}