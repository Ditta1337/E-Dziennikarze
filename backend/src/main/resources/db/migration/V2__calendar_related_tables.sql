CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE subjects
(
    id   uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name varchar(50) NOT NULL
);

CREATE TABLE subjects_taught
(
    id         uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    teacher_id uuid NOT NULL,
    subject_id uuid NOT NULL
);

CREATE TABLE students_groups
(
    id         uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id uuid NOT NULL,
    group_id   uuid NOT NULL
);

CREATE TABLE groups
(
    id         uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_year int         NOT NULL,
    group_code varchar(50) NOT NULL,
    is_class   boolean     NOT NULL
);

CREATE TABLE planned_lessons
(
    id         uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_id uuid        NOT NULL,
    teacher_id uuid        NOT NULL,
    start_time time        NOT NULL,
    end_time   time        NOT NULL,
    active     boolean     NOT NULL,
    week_day   varchar(10) NOT NULL,
    room_id    uuid        NOT NULL,
    group_id   uuid        NOT NULL
);

CREATE TABLE assigned_lessons
(
    id                uuid    NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    planned_lesson_id uuid,
    date              date    NOT NULL,
    is_cancelled      boolean NOT NULL,
    is_modified       boolean NOT NULL
);

CREATE TABLE modified_lessons
(
    id                 uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    assigned_lesson_id uuid NOT NULL,
    teacher_id         uuid NOT NULL,
    start_time         time NOT NULL,
    end_time           time NOT NULL,
    group_id           uuid NOT NULL,
    room_id            uuid NOT NULL,
    subject_id         uuid NOT NULL
);

CREATE TABLE rooms
(
    id        uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    capacity  int         NOT NULL,
    room_code varchar(20) NOT NULL
);

CREATE TABLE attendances
(
    id         uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    lesson_id  uuid NOT NULL,
    present    boolean
);

ALTER TABLE subjects_taught
    ADD CONSTRAINT subjects_taught_teacher_fk
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE subjects_taught
    ADD CONSTRAINT subjects_taught_subject_fk
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;

ALTER TABLE students_groups
    ADD CONSTRAINT students_groups_student_fk
        FOREIGN KEY (student_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE students_groups
    ADD CONSTRAINT students_groups_group_fk
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE CASCADE;

ALTER TABLE assigned_lessons
    ADD CONSTRAINT assigned_subjects_planned_subjects
        FOREIGN KEY (planned_lesson_id)
            REFERENCES planned_lessons (id)
            ON DELETE CASCADE;

ALTER TABLE attendances
    ADD CONSTRAINT attendances_assigned_lessons
        FOREIGN KEY (lesson_id)
            REFERENCES assigned_lessons (id)
            ON DELETE CASCADE;

ALTER TABLE attendances
    ADD CONSTRAINT attendances_users
        FOREIGN KEY (student_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE attendances
    ADD CONSTRAINT attendances_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT modified_lessons_assigned_lessons
        FOREIGN KEY (assigned_lesson_id)
            REFERENCES assigned_lessons (id)
            ON DELETE CASCADE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT modified_lessons_groups
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE CASCADE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT modified_lessons_rooms
        FOREIGN KEY (room_id)
            REFERENCES rooms (id)
            ON DELETE CASCADE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT modified_lessons_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT modified_lessons_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE planned_lessons
    ADD CONSTRAINT planned_lessons_groups
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON DELETE CASCADE;

ALTER TABLE planned_lessons
    ADD CONSTRAINT planned_lessons_rooms
        FOREIGN KEY (room_id)
            REFERENCES rooms (id)
            ON DELETE CASCADE;

ALTER TABLE planned_lessons
    ADD CONSTRAINT planned_lessons_users
        FOREIGN KEY (teacher_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE planned_lessons
    ADD CONSTRAINT planned_subjects_subjects
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            ON DELETE CASCADE;