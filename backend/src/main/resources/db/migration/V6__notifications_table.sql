CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE notifications
(
    id          uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     uuid        NOT NULL,
    message     text        NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now(),
    read        boolean     NOT NULL DEFAULT false
);

ALTER TABLE notifications
    ADD CONSTRAINT notifications_users
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;