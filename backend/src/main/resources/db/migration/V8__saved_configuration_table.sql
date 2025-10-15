CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE plan_configurations
(
    id               uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at       timestamptz NOT NULL             DEFAULT now(),
    name             varchar(50) NOT NULL,
    office_worker_id uuid        NOT NULL,
    configuration    text        NOT NULL,
    calculated       boolean     NOT NULL
);

ALTER TABLE plan_configurations
    ADD CONSTRAINT grades_users
        FOREIGN KEY (office_worker_id)
            REFERENCES users (id)
            ON DELETE CASCADE;