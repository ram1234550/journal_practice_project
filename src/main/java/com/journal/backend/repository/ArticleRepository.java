package com.journal.backend.repository;

import com.journal.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByAuthorId(Long authorId);

    List<Article> findByStatus(String status);

    List<Article> findByStatusOrderByCreatedAtDesc(String status);

    List<Article> findByStatusAndTopicIgnoreCaseOrderByCreatedAtDesc(String status, String topic);

    List<Article> findByReviewerIdAndStatus(Long reviewerId, String status);
}
