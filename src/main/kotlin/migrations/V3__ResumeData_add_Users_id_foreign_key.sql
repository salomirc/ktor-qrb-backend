-- 1. Add column nullable
ALTER TABLE ResumeData ADD user_id INT NULL;

-- 2. Assign user IDs for existing rows (adjust as needed)
UPDATE ResumeData SET user_id = 1;

-- 3. Make column NOT NULL
ALTER TABLE ResumeData MODIFY user_id INT NOT NULL;

-- 4. Add the foreign key
ALTER TABLE ResumeData
    ADD CONSTRAINT fk_resume_data_user_id
    FOREIGN KEY (user_id)
        REFERENCES Users(id)
        ON DELETE CASCADE;
