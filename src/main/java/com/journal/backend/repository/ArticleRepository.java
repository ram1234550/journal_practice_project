package com.journal.backend.repository;

import com.journal.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Все статьи конкретного автора
    List<Article> findByAuthorId(Long authorId);

    // Все статьи с определённым статусом
    List<Article> findByStatus(String status);

    // Статьи назначенные конкретному рецензенту с нужным статусом
    List<Article> findByReviewerIdAndStatus(Long reviewerId, String status);
}