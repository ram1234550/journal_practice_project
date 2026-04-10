package com.journal.backend.repository;

import com.journal.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Все рецензии на конкретную статью
    List<Review> findByArticleId(Long articleId);
}