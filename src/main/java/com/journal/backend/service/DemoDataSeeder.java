package com.journal.backend.service;

import com.journal.backend.entity.Article;
import com.journal.backend.entity.Review;
import com.journal.backend.entity.User;
import com.journal.backend.repository.ArticleRepository;
import com.journal.backend.repository.ReviewRepository;
import com.journal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!seedDemoData) {
            return;
        }

        ensureUser("Editor Admin", "admin@journal.local", "admin123", "ADMIN");
        User reviewer = ensureUser("Rina Reviewer", "reviewer@journal.local", "reviewer123", "REVIEWER");
        User author = ensureUser("Aidar Author", "author@journal.local", "author123", "AUTHOR");

        if (articleRepository.count() > 0) {
            return;
        }

        Article published = createArticle(
                author,
                reviewer,
                "Peer Review for Practical Journals",
                "Editorial Workflow",
                "This sample article demonstrates a published item inside the MVP journal. It exists so the public catalog is not empty on first run.",
                "PUBLISHED",
                10
        );

        createArticle(
                author,
                null,
                "Building a Lightweight Journal Platform",
                "Software Engineering",
                "This article is waiting for an editor to assign a reviewer. It is useful for demonstrating the admin dashboard on a fresh deployment.",
                "PENDING",
                5
        );

        createArticle(
                author,
                reviewer,
                "Open Rubrics for Student Research",
                "Education",
                "This article is currently under review and appears in the reviewer's queue so the reviewer flow is visible immediately.",
                "UNDER_REVIEW",
                2
        );

        Article revision = createArticle(
                author,
                null,
                "Revising Academic Writing with Checklists",
                "Writing",
                "This sample article has been returned for revision so the author dashboard can show the resubmission flow.",
                "REVISION",
                1
        );

        createReview(published, reviewer, "ACCEPTED", "Ready for publication.");
        createReview(revision, reviewer, "REVISION", "Please clarify the evaluation criteria and strengthen the conclusion.");
    }

    private User ensureUser(String name, String email, String rawPassword, String role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    private Article createArticle(User author,
                                  User reviewer,
                                  String title,
                                  String topic,
                                  String content,
                                  String status,
                                  int daysAgo) {
        LocalDateTime timestamp = LocalDateTime.now().minusDays(daysAgo);

        Article article = new Article();
        article.setAuthor(author);
        article.setReviewer(reviewer);
        article.setTitle(title);
        article.setTopic(topic);
        article.setContent(content);
        article.setStatus(status);
        article.setCreatedAt(timestamp);
        article.setUpdatedAt(timestamp.plusHours(2));
        return articleRepository.save(article);
    }

    private void createReview(Article article, User reviewer, String verdict, String comment) {
        Review review = new Review();
        review.setArticle(article);
        review.setReviewer(reviewer);
        review.setVerdict(verdict);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now().minusHours(12));
        reviewRepository.save(review);
    }
}
