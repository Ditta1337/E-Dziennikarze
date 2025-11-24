CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE manual_plans
(
    id                  uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                varchar(50) NOT NULL,
    office_worker_id    uuid        NOT NULL,
    plan_calculation_id uuid,
    created_at          timestamptz NOT NULL             DEFAULT now(),
    plan                text        NOT NULL,
    errors              text
);

ALTER TABLE manual_plans
    ADD CONSTRAINT manual_plans_plan_calculations_fk
        FOREIGN KEY (plan_calculation_id)
            REFERENCES plan_calculations (id)
            ON DELETE SET NULL;