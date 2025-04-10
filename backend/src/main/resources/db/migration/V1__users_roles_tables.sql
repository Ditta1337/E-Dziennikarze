CREATE TABLE users
(
    id           uuid PRIMARY KEY,
    name         varchar(40)  NOT NULL,
    surname      varchar(40)  NOT NULL,
    created_at   date         NOT NULL,
    address      varchar(50)  NOT NULL,
    email        varchar(320) NOT NULL,
    password     varchar(20)  NOT NULL,
    contact      text         NOT NULL,
    image_base64 text,
    role         varchar(20)  NOT NULL,
    is_active    boolean      NOT NULL
);

CREATE TABLE admins
(
    id      uuid PRIMARY KEY,
    user_id uuid UNIQUE NOT NULL
);

CREATE TABLE office_workers
(
    id                   uuid PRIMARY KEY,
    user_id              uuid UNIQUE NOT NULL,
    principal_priviledge boolean     NOT NULL
);

CREATE TABLE guardians
(
    id      uuid PRIMARY KEY,
    user_id uuid NOT NULL
);

CREATE TABLE students
(
    id                     uuid PRIMARY KEY,
    user_id                uuid UNIQUE NOT NULL,
    guardian_id            uuid,
    can_choose_preferences boolean     NOT NULL
);

ALTER TABLE office_workers
    ADD CONSTRAINT office_worker_to_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE admins
    ADD CONSTRAINT admin_to_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE guardians
    ADD CONSTRAINT guardian_to_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;

ALTER TABLE students
    ADD CONSTRAINT student_to_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE,
    ADD CONSTRAINT student_to_guardian
        FOREIGN KEY (guardian_id)
            REFERENCES guardians (id)
            NOT DEFERRABLE
                INITIALLY IMMEDIATE;