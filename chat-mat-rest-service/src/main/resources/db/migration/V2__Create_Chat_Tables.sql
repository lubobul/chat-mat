CREATE TABLE friends
(
    user_id    BIGINT NOT NULL,
    friend_id  BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE chats
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,   -- Chat or channel name
    is_channel BOOLEAN   DEFAULT FALSE, -- True if it has more than 2 participants
    owner_id   BIGINT       NOT NULL,   -- The user who owns the chat
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE chat_participants
(
    chat_id   BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    is_admin  BOOLEAN   DEFAULT FALSE, -- True if the user is an admin
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (chat_id, user_id),
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE chat_messages
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_id    BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL, -- The user who sent the message
    message    TEXT   NOT NULL, -- The message content
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
);


