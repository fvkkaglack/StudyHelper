CREATE TABLE IF NOT EXISTS users
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    nickname character varying COLLATE pg_catalog."default" NOT NULL,
    balance integer NOT NULL DEFAULT 0,
    total_stars integer NOT NULL DEFAULT 0,
    debt integer NOT NULL DEFAULT 0,
    tasks_created integer NOT NULL DEFAULT 0,
    tasks_taken integer NOT NULL DEFAULT 0,
    overdue_fake_tasks integer NOT NULL DEFAULT 0,
    unjust_rejections integer NOT NULL DEFAULT 0,
    is_task_creation_blocked boolean NOT NULL DEFAULT false,
    is_task_taking_blocked boolean NOT NULL DEFAULT false,
    block_until timestamp without time zone,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);