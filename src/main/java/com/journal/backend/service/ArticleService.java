package com.journal.backend.service;

import com.journal.backend.dto.ArticleResponseDTO;
import com.journal.backend.dto.AssignReviewerRequest;
import com.journal.backend.dto.CreateArticleRequest;
import com.journal.backend.dto.ReviewDecisionRequest;
import com.journal.backend.dto.UserSummaryDTO;
import com.journal.backend.entity.Article;
import com.journal.backend.entity.Review;
import com.journal.backend.entity.User;
import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.ReviewRepository;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    public ArticleResponseDTO createArticle(String authorEmail, CreateArticleRequest request) {
        User author = requireUserByEmail(authorEmail);

        Article article = new Article();
        article.setAuthor(author);
        article.setTitle(request.getTitle());
        article.setTopic(request.getTopic());
        article.setContent(request.getContent());
        article.setStatus("PENDING");
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        return toAuthorArticle(articleRepository.save(article));
    }

    public ArticleResponseDTO assignReviewer(AssignReviewerRequest request) {
        Article article = requireArticle(request.getArticleId());
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Рецензент не найден"));

        if (!reviewer.hasRole("REVIEWER")) {
            throw new ResponseStatusException(BAD_REQUEST, "Этот пользователь не является рецензентом");
        }

        article.setReviewer(reviewer);
        article.setStatus("UNDER_REVIEW");
        article.setUpdatedAt(LocalDateTime.now());

        return toAdminArticle(articleRepository.save(article));
    }

    public ArticleResponseDTO submitReview(String reviewerEmail, ReviewDecisionRequest request) {
        Article article = requireArticle(request.getArticleId());
        User reviewer = requireUserByEmail(reviewerEmail);

        if (article.getReviewer() == null || !article.getReviewer().getId().equals(reviewer.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Статья не назначена этому рецензенту");
        }

        Review review = new Review();
        review.setArticle(article);
        review.setReviewer(reviewer);
        review.setVerdict(request.getVerdict());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        if ("ACCEPTED".equals(request.getVerdict())) {
            article.setStatus("PUBLISHED");
        } else {
            article.setStatus("REVISION");
        }

        article.setUpdatedAt(LocalDateTime.now());
        Article savedArticle = articleRepository.save(article);

        return toBlindArticle(savedArticle);
    }

    public ArticleResponseDTO resubmitArticle(String authorEmail, Long articleId, String newContent) {
        Article article = requireArticle(articleId);
        User author = requireUserByEmail(authorEmail);

        if (article.getAuthor() == null || !article.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Нельзя отправить чужую статью на доработку");
        }

        if (!"REVISION".equals(article.getStatus())) {
            throw new ResponseStatusException(BAD_REQUEST, "Статья не находится на доработке");
        }

        article.setContent(newContent);
        article.setStatus("PENDING");
        article.setReviewer(null);
        article.setUpdatedAt(LocalDateTime.now());

        return toAuthorArticle(articleRepository.save(article));
    }

    public List<ArticleResponseDTO> getPendingArticles() {
        return articleRepository.findByStatus("PENDING").stream()
                .map(this::toAdminArticle)
                .toList();
    }

    public List<UserSummaryDTO> getReviewers() {
        return userRepository.findByRole("REVIEWER").stream()
                .map(user -> new UserSummaryDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getRoles()))
                .toList();
    }

    public List<ArticleResponseDTO> getArticlesForReviewer(String reviewerEmail) {
        User reviewer = requireUserByEmail(reviewerEmail);
        return articleRepository.findByReviewerIdAndStatus(reviewer.getId(), "UNDER_REVIEW").stream()
                .map(this::toBlindArticle)
                .toList();
    }

    public List<ArticleResponseDTO> getPublishedArticles(String topic) {
        List<Article> articles = topic == null || topic.isBlank()
                ? articleRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED")
                : articleRepository.findByStatusAndTopicIgnoreCaseOrderByCreatedAtDesc("PUBLISHED", topic.trim());

        return articles.stream()
                .map(this::toPublicArticle)
                .toList();
    }

    public ArticleResponseDTO getPublishedArticle(Long articleId) {
        Article article = requireArticle(articleId);
        if (!"PUBLISHED".equals(article.getStatus())) {
            throw new ResponseStatusException(NOT_FOUND, "Статья не найдена");
        }
        return toPublicArticle(article);
    }

    public List<ArticleResponseDTO> getArticlesByAuthor(String authorEmail) {
        User author = requireUserByEmail(authorEmail);
        return articleRepository.findByAuthorId(author.getId()).stream()
                .map(this::toAuthorArticle)
                .toList();
    }

    private User requireUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));
    }

    private Article requireArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Статья не найдена"));
    }

    private ArticleResponseDTO toAdminArticle(Article article) {
        ArticleResponseDTO dto = toBaseArticle(article);
        if (article.getAuthor() != null) {
            dto.setAuthorName(article.getAuthor().getName());
            dto.setAuthorEmail(article.getAuthor().getEmail());
        }
        return dto;
    }

    private ArticleResponseDTO toAuthorArticle(Article article) {
        ArticleResponseDTO dto = toBaseArticle(article);
        if (article.getAuthor() != null) {
            dto.setAuthorName(article.getAuthor().getName());
            dto.setAuthorEmail(article.getAuthor().getEmail());
        }
        return dto;
    }

    private ArticleResponseDTO toBlindArticle(Article article) {
        return toBaseArticle(article);
    }

    private ArticleResponseDTO toPublicArticle(Article article) {
        ArticleResponseDTO dto = toBaseArticle(article);
        if (article.getAuthor() != null) {
            dto.setAuthorName(article.getAuthor().getName());
        }
        return dto;
    }

    private ArticleResponseDTO toBaseArticle(Article article) {
        ArticleResponseDTO dto = new ArticleResponseDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setTopic(article.getTopic());
        dto.setContent(article.getContent());
        dto.setStatus(article.getStatus());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        if (article.getReviewer() != null) {
            dto.setReviewerName(article.getReviewer().getName());
        }
        return dto;
    }
}
