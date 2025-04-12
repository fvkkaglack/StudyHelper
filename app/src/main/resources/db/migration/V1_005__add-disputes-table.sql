CREATE TABLE IF NOT EXISTS disputes
(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL,
    complainant_id UUID NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    resolution TEXT,
    CONSTRAINT disputes_pkey PRIMARY KEY (id),
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_complainant FOREIGN KEY (complainant_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'RESOLVED', 'ESCALATED'))
);