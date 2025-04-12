CREATE TABLE IF NOT EXISTS chats
(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL,
    author_id UUID NOT NULL,
    executor_id UUID NOT NULL,
    messages TEXT NOT NULL DEFAULT '',
    CONSTRAINT chats_pkey PRIMARY KEY (id),
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_executor FOREIGN KEY (executor_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT unique_task UNIQUE (task_id)
);