CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id                     uuid         NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name                   varchar(40)  NOT NULL,
    surname                varchar(40)  NOT NULL,
    created_at             date         NOT NULL,
    address                varchar(50)  NOT NULL,
    email                  varchar(320) NOT NULL,
    password               varchar(20)  NOT NULL,
    contact                text         NOT NULL,
    image_base64           text,
    role                   varchar(20)  NOT NULL,
    is_active              boolean      NOT NULL,
    can_choose_preferences boolean      NOT NULL
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
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE student_guardians
    ADD CONSTRAINT student_guardians_users_student
        FOREIGN KEY (student_id)
            REFERENCES users (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;


