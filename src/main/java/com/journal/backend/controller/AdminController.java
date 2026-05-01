package com.journal.backend.controller;

import com.journal.backend.dto.AssignReviewerRequest;
import com.journal.backend.entity.Article;
import com.journal.backend.entity.User;
import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.UserRepository;
import com.journal.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;  // ← раскомментировал

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/articles/pending")
    public List<Article> getPendingArticles() {
        return articleService.getPendingArticles();
    }

    @GetMapping("/reviewers")
    public List<User> getAllReviewers() {
        return userRepository.findByRole("REVIEWER");
    }

    @PostMapping("/assign")
    public Article assignReviewer(@RequestBody AssignReviewerRequest request) {
        return articleService.assignReviewer(request);
    }

    @DeleteMapping("/articles/{id}")
    public Map<String, String> deleteArticle(@PathVariable Long id) {
        articleRepository.deleteById(id);
        return Map.of("message", "Статья удалена");
    }
}