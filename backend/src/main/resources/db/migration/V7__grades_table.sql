CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE grades
(
    id         uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id uuid        NOT NULL,
    subject_id uuid        NOT NULL,
    grade      float       NOT NULL,
    weight     float       NOT NULL,
    is_final   boolean     NOT NULL             DEFAULT false,
    created_at timestamptz NOT NULL             DEFAULT now()
);

ALTER TABLE grades
    ADD CONSTRAINT grades_users
        FOREIGN KEY (student_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE grades
    ADD CONSTRAINT grades_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;