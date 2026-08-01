ALTER TABLE transaction
    ADD COLUMN category_id BIGINT;

ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id)
            REFERENCES categories(id);

CREATE INDEX idx_transaction_category_id
    ON transaction(category_id);