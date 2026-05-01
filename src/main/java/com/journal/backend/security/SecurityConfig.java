package com.journal.backend.security;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // отключаем CSRF — не нужен для REST API
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // без сессий — используем JWT
//                .authorizeHttpRequests(auth -> auth
//
//                        // Эти эндпоинты открыты для всех — логин и регистрация
//                        .requestMatchers("/api/users/register", "/api/auth/login").permitAll()
//
//                        // Только ADMIN может заходить в /api/admin/**
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // Только REVIEWER может отправлять рецензии
//                        .requestMatchers("/api/articles/submit-review").hasRole("REVIEWER")
//
//                        // Только AUTHOR может создавать статьи и отправлять доработки
//                        .requestMatchers("/api/articles").hasRole("AUTHOR")
//                        .requestMatchers("/api/articles/*/resubmit").hasRole("AUTHOR")
//
//                        // Все остальные запросы требуют авторизации (любой роли)
//                        .anyRequest().authenticated()
//                )
//                // Добавляем наш JWT-фильтр перед стандартным фильтром Spring
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← добавь
                .csrf(csrf -> csrf.disable())
                // ... остальное без изменений
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Открываем регистрацию и логин полностью
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/articles/published").permitAll()
                        .requestMatchers("/api/articles/author/**").authenticated()

                        // Защищённые эндпоинты по ролям
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/articles/submit-review").hasRole("REVIEWER")

                        // Всё остальное требует авторизации
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt — алгоритм хеширования паролей
    // Пароль "12345" → "$2a$10$xK8..." — обратно не расшифровать
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE",  "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        return new UrlBasedCorsConfigurationSource(){{
            registerCorsConfiguration("/**", config);
        }};
    }
}