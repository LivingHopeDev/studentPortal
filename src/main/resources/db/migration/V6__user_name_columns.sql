-- ============================================================
-- V6: Add first_name / last_name columns to users
-- ============================================================

ALTER TABLE users ADD COLUMN first_name VARCHAR(255);
ALTER TABLE users ADD COLUMN last_name VARCHAR(255);

UPDATE users SET
    first_name = split_part(full_name, ' ', 1),
    last_name = CASE
        WHEN position(' ' in full_name) > 0 THEN substring(full_name from position(' ' in full_name) + 1)
        ELSE full_name
    END;

ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;
