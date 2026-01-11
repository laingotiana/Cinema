-- Test user for application login
-- Usage: copy to /tmp and run as postgres or run as a DB superuser

-- Ensure pgcrypto is available (needed for crypt/gen_salt)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Replace values as needed
-- Test credentials:
--   email: test@cinema.test
--   password: Secret123!

-- Insert user if not exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM personne WHERE email = 'test@cinema.test') THEN
        INSERT INTO personne (nom_complet, email, telephone, mot_de_passe_hash, role, cree_le)
        VALUES (
            'Utilisateur Test',
            'test@cinema.test',
            NULL,
            crypt('Secret123!', gen_salt('bf', 10)),
            'USER',
            now()
        );
    ELSE
        -- Update password for existing user (safe to run multiple times)
        UPDATE personne
        SET mot_de_passe_hash = crypt('Secret123!', gen_salt('bf', 10))
        WHERE email = 'test@cinema.test';
    END IF;
END$$;

-- Verify: select id,email from personne where email='test@cinema.test';
