-- Вставка администратора в таблицу users
INSERT INTO users (username, email, password, role, created_at, updated_at)
VALUES
    ('admin', 'admin@example.com', 'admin', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);