ALTER TABLE attendances
    ADD COLUMN status VARCHAR(10);

UPDATE attendances
SET status = CASE
        WHEN present = TRUE THEN 'PRESENT'
        ELSE 'ABSENT'
END;

ALTER TABLE attendances
    ALTER COLUMN status SET NOT NULL,
    DROP COLUMN present;