CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id uuid NOT NULL,
    group_id uuid NOT NULL
);

CREATE TABLE groups
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_year int NOT NULL,
    group_letter varchar(10),
    is_class boolean NOT NULL
);

CREATE TABLE planned_lessons
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_id uuid NOT NULL,
    teacher_id uuid NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    is_active boolean NOT NULL,
    week_day varchar(10) NOT NULL,
    room_id uuid NOT NULL,
    group_id uuid NOT NULL
);

CREATE TABLE assigned_lessons
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    planned_lesson_id uuid,
    date date NOT NULL,
    is_cancelled boolean NOT NULL,
    is_modified boolean NOT NULL
);

CREATE TABLE modified_lessons
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    assigned_lesson_id uuid NOT NULL,
    teacher_id uuid NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    group_id uuid NOT NULL
);

CREATE TABLE rooms
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    capacity int NOT NULL,
    room_code varchar(20)
);

CREATE TABLE attendance
(
    id uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id uuid NOT NULL,
    lesson_id uuid NOT NULL,
    present boolean
);

ALTER TABLE subjects_taught
    ADD CONSTRAINT teachers_to_subject_taught
        FOREIGN KEY (teacher_id)
            REFERENCES teachers (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT subjects_to_subject_taught
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE students_groups
    ADD CONSTRAINT students_to_students_groups
        FOREIGN KEY (student_id)
            REFERENCES students (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT groups_to_student_groups
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE planned_lessons
    ADD CONSTRAINT subjects_to_planned_lessons
        FOREIGN KEY (subject_id)
            REFERENCES subjects (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT teachers_to_planned_lessons
        FOREIGN KEY (teacher_id)
            REFERENCES teachers (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT rooms_to_planned_lessons
        FOREIGN KEY (room_id)
            REFERENCES rooms (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT groups_to_planned_lessons
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE assigned_lessons
    ADD CONSTRAINT planned_lessons_to_assigned_lessons
        FOREIGN KEY (planned_lesson_id)
            REFERENCES planned_lessons (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE modified_lessons
    ADD CONSTRAINT assigned_lessons_to_modified_lessons
        FOREIGN KEY (assigned_lesson_id)
            REFERENCES assigned_lessons (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT teachers_to_modified_lessons
        FOREIGN KEY (teacher_id)
            REFERENCES teachers (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT groups_to_modified_lessons
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE attendance
    ADD CONSTRAINT students_to_attendance
        FOREIGN KEY (student_id)
            REFERENCES students (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT lessons_to_attendance
        FOREIGN KEY (lesson_id)
            REFERENCES assigned_lessons (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;