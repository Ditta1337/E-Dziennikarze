CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE messages
(
    id          uuid        NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id   uuid        NOT NULL,
    receiver_id uuid        NOT NULL,
    content     text,
    type        varchar(10) NOT NULL,
    status      varchar(10) NOT NULL,
    file_path   text,
    created_at  timestamptz NOT NULL             DEFAULT now(),
    updated_at  timestamptz
);

CREATE INDEX idx_messages_sender_receiver
    ON messages (sender_id, receiver_id, created_at DESC);

CREATE INDEX idx_messages_receiver_sender
    ON messages (receiver_id, sender_id, created_at DESC);

ALTER TABLE messages
    ADD CONSTRAINT messages_sender_users
        FOREIGN KEY (sender_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE messages
    ADD CONSTRAINT messages_receiver_users
        FOREIGN KEY (receiver_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

