CREATE TABLE refresh_token (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,

                               token VARCHAR(512) NOT NULL UNIQUE,

                               expiry_date DATETIME NOT NULL,

                               revoked BOOLEAN NOT NULL DEFAULT FALSE,

                               user_id BIGINT NOT NULL,

                               CONSTRAINT fk_refresh_token_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user_id
    ON refresh_token(user_id);

CREATE INDEX idx_refresh_token_expiry
    ON refresh_token(expiry_date);
