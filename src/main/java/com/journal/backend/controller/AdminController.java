package com.journal.backend.controller;

import com.journal.backend.dto.ArticleResponseDTO;
import com.journal.backend.dto.AssignReviewerRequest;
import com.journal.backend.dto.UserSummaryDTO;
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

    @GetMapping("/articles/pending")
    public List<ArticleResponseDTO> getPendingArticles() {
        return articleService.getPendingArticles();
    }

    @GetMapping("/reviewers")
    public List<UserSummaryDTO> getAllReviewers() {
        return articleService.getReviewers();
    }

    @PostMapping("/assign")
    public ArticleResponseDTO assignReviewer(@RequestBody AssignReviewerRequest request) {
        return articleService.assignReviewer(request);
    }
}
