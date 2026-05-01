package com.journal.backend.controller;

import com.journal.backend.dto.ArticleResponseDTO;
import com.journal.backend.dto.CreateArticleRequest;
import com.journal.backend.dto.ResubmitArticleRequest;
import com.journal.backend.dto.ReviewDecisionRequest;
import com.journal.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ArticleResponseDTO createArticle(@RequestBody CreateArticleRequest request,
                                            Authentication authentication) {
        return articleService.createArticle(authentication.getName(), request);
    }

    @PostMapping("/submit-review")
    public ArticleResponseDTO submitReview(@RequestBody ReviewDecisionRequest request,
                                           Authentication authentication) {
        return articleService.submitReview(authentication.getName(), request);
    }

    @GetMapping("/reviewer/me")
    public List<ArticleResponseDTO> getArticlesForReviewer(Authentication authentication) {
        return articleService.getArticlesForReviewer(authentication.getName());
    }

    @PutMapping("/{id}/resubmit")
    public ArticleResponseDTO resubmitArticle(@PathVariable Long id,
                                              @RequestBody ResubmitArticleRequest request,
                                              Authentication authentication) {
        return articleService.resubmitArticle(authentication.getName(), id, request.getContent());
    }

    @GetMapping("/published")
    public List<ArticleResponseDTO> getPublishedArticles() {
        return articleService.getPublishedArticles();
    }

    @GetMapping("/published/{id}")
    public ArticleResponseDTO getPublishedArticle(@PathVariable Long id) {
        return articleService.getPublishedArticle(id);
    }

    @GetMapping("/my")
    public List<ArticleResponseDTO> getMyArticles(Authentication authentication) {
        return articleService.getArticlesByAuthor(authentication.getName());
    }
}
