CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS articles (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT REFERENCES users(id),
    reviewer_id BIGINT REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    topic VARCHAR(255),
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT REFERENCES articles(id),
    reviewer_id BIGINT REFERENCES users(id),
    verdict VARCHAR(50),
    comment TEXT,
    created_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_articles_status ON articles(status);
CREATE INDEX IF NOT EXISTS idx_articles_author_id ON articles(author_id);
CREATE INDEX IF NOT EXISTS idx_articles_reviewer_id ON articles(reviewer_id);
CREATE INDEX IF NOT EXISTS idx_reviews_article_id ON reviews(article_id);
