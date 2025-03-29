CREATE TABLE IF NOT EXISTS public."order"
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name character varying NOT NULL,
    description character varying NOT NULL,
    deadline timestamp without time zone NOT NULL,
    reward double precision NOT NULL,
    CONSTRAINT order_pkey PRIMARY KEY (id)
)