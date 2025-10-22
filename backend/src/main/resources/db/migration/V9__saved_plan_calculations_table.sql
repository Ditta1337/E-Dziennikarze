CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE plan_calculations
(
    id          uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        varchar(50) NOT NULL,
    plan_id     uuid        NOT NULL,
    created_at  timestamptz NOT NULL             DEFAULT now(),
    calculation text        NOT NULL
);

ALTER TABLE plan_calculations
    ADD CONSTRAINT plan_calculations_plan_configurations
        FOREIGN KEY (plan_id)
            REFERENCES plan_configurations (id)
            ON DELETE CASCADE;


