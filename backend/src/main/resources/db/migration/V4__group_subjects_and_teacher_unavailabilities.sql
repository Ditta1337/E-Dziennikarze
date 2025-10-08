CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE group_subjects
(
    id                   uuid    NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    teacher_id           uuid    NOT NULL,
    subject_id           uuid    NOT NULL,
    group_id             uuid    NOT NULL,
    max_lessons_per_week int     NOT NULL,
    max_lessons_per_day  int     NOT NULL,
    active               boolean NOT NULL
);

CREATE TABLE teacher_unavailabilities
(
    id         uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_time time        NOT NULL,
    end_time   time        NOT NULL,
    week_day   varchar(10) NOT NULL,
    teacher_id uuid        NOT NULL
);

ALTER TABLE group_subjects
    ADD CONSTRAINT group_subjects_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE group_subjects
    ADD CONSTRAINT group_subjects_groups
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE CASCADE;

ALTER TABLE group_subjects
    ADD CONSTRAINT group_subjects_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;

ALTER TABLE teacher_unavailabilities
    ADD CONSTRAINT teacher_unavailabilities_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;