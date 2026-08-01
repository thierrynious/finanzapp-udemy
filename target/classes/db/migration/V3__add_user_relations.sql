ALTER TABLE categories
    ADD COLUMN user_id BIGINT;

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

ALTER TABLE transaction
    ADD COLUMN user_id BIGINT;

ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

CREATE INDEX idx_categories_user_id
    ON categories(user_id);

CREATE INDEX idx_transaction_user_id
    ON transaction(user_id);

CREATE INDEX idx_transaction_user_date
    ON transaction(user_id, date);