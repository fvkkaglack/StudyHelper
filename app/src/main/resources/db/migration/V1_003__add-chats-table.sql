CREATE TABLE IF NOT EXISTS chats (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL,
    author_id UUID NOT NULL,
    executor_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chats_pkey PRIMARY KEY (id),
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_executor FOREIGN KEY (executor_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_task UNIQUE (task_id)
);