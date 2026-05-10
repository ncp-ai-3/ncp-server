DELETE FROM member_oauth mo
WHERE NOT EXISTS (
    SELECT 1
    FROM member m
    WHERE m.id = mo.member_id
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_member_oauth_member'
    ) THEN
        ALTER TABLE member_oauth
            ADD CONSTRAINT fk_member_oauth_member
            FOREIGN KEY (member_id)
            REFERENCES member(id)
            ON DELETE CASCADE;
    END IF;
END $$;
