package com.journal.backend.repository;

import com.journal.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<User, Long> означает:
// работаем с таблицей User, первичный ключ типа Long
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring сам напишет SQL для этого метода!
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT * FROM users WHERE role = ?
    java.util.List<User> findByRole(String role);
}