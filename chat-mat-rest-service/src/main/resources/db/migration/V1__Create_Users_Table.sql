CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN   DEFAULT FALSE,
    avatar     TEXT      DEFAULT NULL
);

CREATE TABLE user_secrets
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT       NOT NULL,
    password VARCHAR(255) NOT NULL,
    deleted  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);