CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id                      uuid         NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                    varchar(40)  NOT NULL,
    surname                 varchar(40)  NOT NULL,
    created_at              timestamptz  NOT NULL             DEFAULT now(),
    address                 varchar(50)  NOT NULL,
    email                   varchar(320) NOT NULL,
    password                varchar(255)  NOT NULL,
    contact                 text         NOT NULL,
    image_base64            text,
    role                    varchar(20)  NOT NULL,
    active               boolean      NOT NULL,
    choosing_preferences boolean      NOT NULL
);

CREATE TABLE student_guardians
(
    id          uuid NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id  uuid NOT NULL,
    guardian_id uuid NOT NULL
);

ALTER TABLE student_guardians
    ADD CONSTRAINT student_guardians_users_guardian
        FOREIGN KEY (guardian_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE student_guardians
    ADD CONSTRAINT student_guardians_users_student
        FOREIGN KEY (student_id)
            REFERENCES users (id)
            ON DELETE CASCADE;