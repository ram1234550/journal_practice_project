package com.journal.backend.service;

import com.journal.backend.dto.ArticleResponseDTO;
import com.journal.backend.dto.AssignReviewerRequest;
import com.journal.backend.dto.ReviewDecisionRequest;
import com.journal.backend.entity.Article;
import com.journal.backend.entity.Review;
import com.journal.backend.entity.User;
import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.ReviewRepository;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service   // говорит Spring: это сервис-класс с логикой
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Автор создаёт статью ──────────────────────────────────────────
    public Article createArticle(Long authorId, String title,
                                 String topic, String content) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));

        Article article = new Article();
        article.setAuthor(author);
        article.setTitle(title);
        article.setTopic(topic);
        article.setContent(content);
        article.setStatus("PENDING");   // ждёт проверки у админа
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(article);
    }

    // ── Админ назначает рецензента ────────────────────────────────────
    public Article assignReviewer(AssignReviewerRequest request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Рецензент не найден"));

        // Проверяем что это именно рецензент, а не автор или админ
        if (!reviewer.getRole().equals("REVIEWER")) {
            throw new RuntimeException("Этот пользователь не является рецензентом");
        }

        article.setReviewer(reviewer);
        article.setStatus("UNDER_REVIEW");   // статья на рецензировании
        article.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(article);
    }

    // ── Рецензент получает статью БЕЗ данных автора ───────────────────
    public ArticleResponseDTO getArticleForReviewer(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));

        // Копируем только нужные поля — имя и email автора НЕ включаем
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setTopic(article.getTopic());
        dto.setContent(article.getContent());
        dto.setStatus(article.getStatus());

        return dto;   // автор неизвестен рецензенту
    }

    // ── Рецензент выносит вердикт ─────────────────────────────────────
    public Review submitReview(ReviewDecisionRequest request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Рецензент не найден"));

        // Сохраняем рецензию
        Review review = new Review();
        review.setArticle(article);
        review.setReviewer(reviewer);
        review.setVerdict(request.getVerdict());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        // Меняем статус статьи в зависимости от решения
        if (request.getVerdict().equals("ACCEPTED")) {
            article.setStatus("PUBLISHED");    // статья опубликована
        } else {
            article.setStatus("REVISION");     // отправлена на доработку
        }

        article.setUpdatedAt(LocalDateTime.now());
        articleRepository.save(article);

        return review;
    }

    // ── Автор отправляет доработанную статью ─────────────────────────
    public Article resubmitArticle(Long articleId, String newContent) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));

        if (!article.getStatus().equals("REVISION")) {
            throw new RuntimeException("Статья не находится на доработке");
        }

        article.setContent(newContent);
        article.setStatus("PENDING");       // снова к админу
        article.setReviewer(null);          // рецензент сбрасывается
        article.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(article);
    }

    // ── Получить все статьи со статусом PENDING (для админа) ─────────
    public List<Article> getPendingArticles() {
        return articleRepository.findByStatus("PENDING");
    }

    // ── Получить статьи назначенные рецензенту ────────────────────────
    public List<Article> getArticlesForReviewer(Long reviewerId) {
        return articleRepository.findByReviewerIdAndStatus(reviewerId, "UNDER_REVIEW");
    }
}