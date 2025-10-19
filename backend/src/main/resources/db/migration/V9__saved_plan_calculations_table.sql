CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE plan_calculations
(
    id          uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_id     uuid        NOT NULL,
    created_at  timestamptz NOT NULL             DEFAULT now(),
    calculation text        NOT NULL
);


