
    CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nickname VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    balance INTEGER NOT NULL DEFAULT 50,
    total_stars INTEGER NOT NULL DEFAULT 0,
    debt INTEGER NOT NULL DEFAULT 0,
    tasks_created INTEGER NOT NULL DEFAULT 0,
    tasks_taken INTEGER NOT NULL DEFAULT 0,
    overdue_fake_tasks INTEGER NOT NULL DEFAULT 0,
    unjust_rejections INTEGER NOT NULL DEFAULT 0,
    is_task_creation_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    is_task_taking_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    block_until TIMESTAMP
);