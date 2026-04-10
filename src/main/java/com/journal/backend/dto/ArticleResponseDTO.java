package com.journal.backend.dto;

// Рецензент получает статью БЕЗ имени, email автора — слепое рецензирование
public class ArticleResponseDTO {

    private Long id;
    private String title;
    private String topic;
    private String content;   // только содержимое — автор неизвестен
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}