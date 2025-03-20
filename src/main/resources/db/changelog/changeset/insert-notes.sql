-- Вставка заметки администратора в таблицу notes
INSERT INTO notes (title, content, created_at, updated_at, user_id)
VALUES
    ('Admin Note', 'Hello, ADMIN!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'admin'));