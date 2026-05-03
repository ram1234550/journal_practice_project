package com.journal.backend.controller;

import com.journal.backend.dto.AuthResponseDTO;
import com.journal.backend.entity.User;
import com.journal.backend.repository.UserRepository;
import com.journal.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Неверный email или пароль"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Неверный email или пароль");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());

        return new AuthResponseDTO(
                token,
                user.getRole(),
                user.getRoles(),
                user.getName(),
                String.valueOf(user.getId())
        );
    }
}
