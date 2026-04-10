package com.journal.backend.controller;

import com.journal.backend.dto.AssignReviewerRequest;
import com.journal.backend.entity.Article;
import com.journal.backend.entity.User;
import com.journal.backend.repository.UserRepository;
import com.journal.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/admin/articles/pending — все статьи которые ждут проверки
    @GetMapping("/articles/pending")
    public List<Article> getPendingArticles() {
        return articleService.getPendingArticles();
    }

    // GET /api/admin/reviewers — список всех рецензентов
    @GetMapping("/reviewers")
    public List<User> getAllReviewers() {
        return userRepository.findByRole("REVIEWER");
    }

    // POST /api/admin/assign — назначить рецензента на статью
    @PostMapping("/assign")
    public Article assignReviewer(@RequestBody AssignReviewerRequest request) {
        return articleService.assignReviewer(request);
    }
}