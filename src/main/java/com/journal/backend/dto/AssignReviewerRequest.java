package com.journal.backend.dto;

// Этот класс описывает тело запроса когда админ назначает рецензента
public class AssignReviewerRequest {

    private Long articleId;    // какую статью
    private Long reviewerId;   // кому назначить

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
}