CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE teacher_groups
(
    id         uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    teacher_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    group_id   uuid NOT NULL
);

CREATE TABLE teacher_unavailabilities
(
    id         uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_time time        NOT NULL,
    end_time   time        NOT NULL,
    week_day   varchar(10) NOT NULL,
    teacher_id uuid        NOT NULL
);

ALTER TABLE teacher_groups
    ADD CONSTRAINT teacher_groups_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE teacher_groups
    ADD CONSTRAINT teacher_groups_groups
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE CASCADE;

ALTER TABLE teacher_groups
    ADD CONSTRAINT teacher_groups_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;

ALTER TABLE teacher_unavailabilities
    ADD CONSTRAINT teacher_unavailabilities_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;