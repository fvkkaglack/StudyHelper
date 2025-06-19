CREATE TABLE IF NOT EXISTS requests
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL,
    user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    comment VARCHAR(500), -- Добавлено поле для комментария, опциональное (NULL по умолчанию)
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);