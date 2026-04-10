package com.journal.backend.controller;

import com.journal.backend.entity.Article;

import com.journal.backend.dto.ArticleResponseDTO;
import com.journal.backend.dto.ReviewDecisionRequest;
import com.journal.backend.entity.Review;
import com.journal.backend.service.ArticleService;

import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleService articleService;

    // GET /api/articles — все статьи
    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // GET /api/articles/status/PENDING — статьи по статусу
    @GetMapping("/status/{status}")
    public List<Article> getByStatus(@PathVariable String status) {
        return articleRepository.findByStatus(status);
    }

    // POST /api/articles — автор создаёт статью
    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        article.setStatus("PENDING");           // статья ждёт проверки админом
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    // PUT /api/articles/1/status — админ меняет статус статьи
    @PutMapping("/{id}/status")
    public Article updateStatus(@PathVariable Long id,
                                @RequestParam String status) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статья не найдена"));
        article.setStatus(status);
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    // POST /api/articles/submit — рецензент выносит вердикт
    @PostMapping("/submit-review")
    public Review submitReview(@RequestBody ReviewDecisionRequest request) {
        return articleService.submitReview(request);
    }

    // GET /api/articles/reviewer/1 — статьи назначенные рецензенту
    @GetMapping("/reviewer/{reviewerId}")
    public List<Article> getArticlesForReviewer(@PathVariable Long reviewerId) {
        return articleService.getArticlesForReviewer(reviewerId);
    }

    // GET /api/articles/1/blind — статья без данных автора
    @GetMapping("/{id}/blind")
    public ArticleResponseDTO getBlindArticle(@PathVariable Long id) {
        return articleService.getArticleForReviewer(id);
    }

    // PUT /api/articles/1/resubmit — автор отправляет доработку
    @PutMapping("/{id}/resubmit")
    public Article resubmitArticle(@PathVariable Long id,
                                   @RequestParam String content) {
        return articleService.resubmitArticle(id, content);
    }

    // GET /api/articles/published — все опубликованные статьи (открыто для всех)
    @GetMapping("/published")
    public List<Article> getPublishedArticles() {
        return articleRepository.findByStatus("PUBLISHED");
    }

    // GET /api/articles/author/1 — все статьи конкретного автора
    @GetMapping("/author/{authorId}")
    public List<Article> getArticlesByAuthor(@PathVariable Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }
}