CREATE TABLE IF NOT EXISTS public."user"
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name character varying COLLATE pg_catalog."default" NOT NULL,
    age integer NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
)