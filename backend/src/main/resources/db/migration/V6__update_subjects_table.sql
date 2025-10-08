ALTER TABLE subjects
    ADD COLUMN lessons_per_week INTEGER NOT NULL,
    ADD COLUMN max_lessons_per_day INTEGER NOT NULL;

UPDATE subjects
SET lessons_per_week = 5,
    max_lessons_per_day = 2;