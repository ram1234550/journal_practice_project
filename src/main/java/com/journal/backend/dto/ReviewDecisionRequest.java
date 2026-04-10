package com.journal.backend.dto;

// Этот класс описывает решение рецензента — принять или на доработку
public class ReviewDecisionRequest {

    private Long articleId;
    private Long reviewerId;
    private String verdict;   // "ACCEPTED" или "REVISION"
    private String comment;   // комментарий рецензента

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}