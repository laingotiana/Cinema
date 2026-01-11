-- ------------------------------
-- DONNEES DE TEST - CINEMA
-- À exécuter APRÈS schema.sql
-- ------------------------------

-- Désactiver temporairement les triggers pour insertion rapide
SET session_replication_role = 'replica';

-- ------------------------------
-- FILMS DE TEST
-- ------------------------------
INSERT INTO film (titre, description, duree_minutes, date_sortie, age_min, langue_originale, affiche_url) VALUES
    ('Oppenheimer', 'L''histoire de J. Robert Oppenheimer et le développement de la bombe atomique.', 180, '2023-07-21', 13, 'Anglais', 'https://image.tmdb.org/t/p/w500/oppenheimer.jpg'),
    ('Barbie', 'Barbie et Ken vivent une vie parfaite dans le monde coloré de Barbieland.', 114, '2023-07-21', 6, 'Anglais', 'https://image.tmdb.org/t/p/w500/barbie.jpg'),
    ('Dune: Deuxième Partie', 'Paul Atreides s''unit avec Chani et les Fremen pour se venger.', 166, '2024-03-01', 12, 'Anglais', 'https://image.tmdb.org/t/p/w500/dune2.jpg'),
    ('Le Voyage de Chihiro', 'Une jeune fille découvre un monde magique peuplé d''esprits.', 125, '2001-07-20', 6, 'Japonais', 'https://image.tmdb.org/t/p/w500/chihiro.jpg'),
    ('Inception', 'Un voleur qui s''introduit dans les rêves des gens se voit confier une mission impossible.', 148, '2010-07-16', 13, 'Anglais', 'https://image.tmdb.org/t/p/w500/inception.jpg'),
    ('La La Land', 'Une actrice en herbe et un musicien de jazz tombent amoureux à Los Angeles.', 128, '2016-12-09', 10, 'Anglais', 'https://image.tmdb.org/t/p/w500/lalaland.jpg'),
    ('Parasite', 'Une famille pauvre s''infiltre dans la vie d''une famille riche.', 132, '2019-05-30', 16, 'Coréen', 'https://image.tmdb.org/t/p/w500/parasite.jpg'),
    ('Avengers: Endgame', 'Les Avengers tentent de défaire les actions de Thanos.', 181, '2019-04-26', 13, 'Anglais', 'https://image.tmdb.org/t/p/w500/endgame.jpg');

-- ------------------------------
-- ASSOCIATION FILMS-GENRES
-- ------------------------------
-- Oppenheimer: Drame, Thriller
INSERT INTO film_genre (id_film, id_genre) VALUES (1, 3), (1, 7);

-- Barbie: Comédie, Aventure
INSERT INTO film_genre (id_film, id_genre) VALUES (2, 2), (2, 10);

-- Dune 2: Science-Fiction, Aventure
INSERT INTO film_genre (id_film, id_genre) VALUES (3, 5), (3, 10);

-- Le Voyage de Chihiro: Animation, Aventure
INSERT INTO film_genre (id_film, id_genre) VALUES (4, 8), (4, 10);

-- Inception: Science-Fiction, Thriller, Action
INSERT INTO film_genre (id_film, id_genre) VALUES (5, 5), (5, 7), (5, 1);

-- La La Land: Romance, Drame
INSERT INTO film_genre (id_film, id_genre) VALUES (6, 6), (6, 3);

-- Parasite: Drame, Thriller
INSERT INTO film_genre (id_film, id_genre) VALUES (7, 3), (7, 7);

-- Avengers Endgame: Action, Science-Fiction, Aventure
INSERT INTO film_genre (id_film, id_genre) VALUES (8, 1), (8, 5), (8, 10);

-- ------------------------------
-- SALLES DE TEST
-- ------------------------------
INSERT INTO salle (nom, capacite) VALUES
    ('Salle Atlas', 80),
    ('Salle Premium', 50),
    ('Salle IMAX', 120);

-- ------------------------------
-- PLACES - Salle Atlas (10 rangées × 8 places)
-- ------------------------------
DO $$
DECLARE
    v_rangee CHAR(1);
    v_numero INT;
    v_type_place INT;
BEGIN
    FOR i IN 1..10 LOOP
        v_rangee := CHR(64 + i); -- A, B, C, ... J
        FOR j IN 1..8 LOOP
            -- Rangées A-B: VIP (type_place = 2)
            -- Rangée J: PMR (type_place = 3)
            -- Autres: STANDARD (type_place = 1)
            IF i <= 2 THEN
                v_type_place := 2; -- VIP
            ELSIF i = 10 THEN
                v_type_place := 3; -- PMR
            ELSE
                v_type_place := 1; -- STANDARD
            END IF;
            
            INSERT INTO place (id_salle, rangee, numero, code_place, id_type_place)
            VALUES (1, v_rangee, j, v_rangee || j, v_type_place);
        END LOOP;
    END LOOP;
END $$;

-- ------------------------------
-- PLACES - Salle Premium (5 rangées × 10 places, tout VIP)
-- ------------------------------
DO $$
DECLARE
    v_rangee CHAR(1);
BEGIN
    FOR i IN 1..5 LOOP
        v_rangee := CHR(64 + i); -- A, B, C, D, E
        FOR j IN 1..10 LOOP
            INSERT INTO place (id_salle, rangee, numero, code_place, id_type_place)
            VALUES (2, v_rangee, j, v_rangee || j, 2); -- Tout VIP
        END LOOP;
    END LOOP;
END $$;

-- ------------------------------
-- PLACES - Salle IMAX (12 rangées × 10 places)
-- ------------------------------
DO $$
DECLARE
    v_rangee CHAR(1);
    v_type_place INT;
BEGIN
    FOR i IN 1..12 LOOP
        v_rangee := CHR(64 + i); -- A, B, ..., L
        FOR j IN 1..10 LOOP
            -- Rangées A-C: VIP
            -- Rangée L: PMR
            -- Autres: STANDARD
            IF i <= 3 THEN
                v_type_place := 2; -- VIP
            ELSIF i = 12 THEN
                v_type_place := 3; -- PMR
            ELSE
                v_type_place := 1; -- STANDARD
            END IF;
            
            INSERT INTO place (id_salle, rangee, numero, code_place, id_type_place)
            VALUES (3, v_rangee, j, v_rangee || j, v_type_place);
        END LOOP;
    END LOOP;
END $$;

-- ------------------------------
-- SEANCES DE TEST (semaine prochaine)
-- ------------------------------
-- Oppenheimer - Salle IMAX
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (1, 3, (now() + INTERVAL '1 day')::DATE + TIME '14:00', 'VF'),
    (1, 3, (now() + INTERVAL '1 day')::DATE + TIME '20:00', 'VO'),
    (1, 3, (now() + INTERVAL '2 days')::DATE + TIME '18:00', 'VF');

-- Barbie - Salle Atlas
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (2, 1, (now() + INTERVAL '1 day')::DATE + TIME '10:30', 'VF'),
    (2, 1, (now() + INTERVAL '1 day')::DATE + TIME '16:00', 'VF'),
    (2, 1, (now() + INTERVAL '2 days')::DATE + TIME '14:00', 'VF');

-- Dune 2 - Salle IMAX
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (3, 3, (now() + INTERVAL '2 days')::DATE + TIME '19:30', 'VO'),
    (3, 3, (now() + INTERVAL '3 days')::DATE + TIME '21:00', 'VF');

-- Le Voyage de Chihiro - Salle Premium
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (4, 2, (now() + INTERVAL '1 day')::DATE + TIME '11:00', 'VF'),
    (4, 2, (now() + INTERVAL '3 days')::DATE + TIME '15:00', 'VO');

-- Inception - Salle Atlas
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (5, 1, (now() + INTERVAL '2 days')::DATE + TIME '21:00', 'VO');

-- La La Land - Salle Premium
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (6, 2, (now() + INTERVAL '1 day')::DATE + TIME '18:30', 'VO');

-- Parasite - Salle Atlas
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (7, 1, (now() + INTERVAL '3 days')::DATE + TIME '20:30', 'VO');

-- Avengers Endgame - Salle IMAX
INSERT INTO seance (id_film, id_salle, debut, langue) VALUES
    (8, 3, (now() + INTERVAL '1 day')::DATE + TIME '17:00', 'VF'),
    (8, 3, (now() + INTERVAL '3 days')::DATE + TIME '19:00', 'VO');

-- ------------------------------
-- UTILISATEURS DE TEST
-- ------------------------------
-- Mot de passe pour tous: test123
-- Hash bcrypt: $2a$10$rM/zV5L5xJ5L5L5L5L5L5uK5L5L5L5L5L5L5L5L5L5L5L5L5Le
INSERT INTO personne (nom_complet, email, telephone, mot_de_passe_hash, role) VALUES
    ('Jean Dupont', 'jean.dupont@email.com', '0341234567', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
    ('Marie Martin', 'marie.martin@email.com', '0347654321', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
    ('Pierre Dubois', 'pierre.dubois@email.com', '0349876543', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
    ('Sophie Lambert', 'sophie.lambert@email.com', '0342468135', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');

-- ------------------------------
-- TARIFS SPECIFIQUES (PROMOTIONS)
-- ------------------------------
-- Promotion enfant pour séance Barbie du matin (id_seance = 4)
INSERT INTO tarif_seance (id_seance, id_type_place, id_categorie_personne, prix) VALUES
    (4, 1, 2, 4.50); -- STANDARD ENFANT à 4.50€ au lieu de 6€

-- Promotion pour Le Voyage de Chihiro (id_seance = 10)
INSERT INTO tarif_seance (id_seance, id_type_place, id_categorie_personne, prix) VALUES
    (10, 2, 2, 7.00); -- VIP ENFANT à 7€ au lieu de 10€

-- ------------------------------
-- RESERVATIONS ET TICKETS DE TEST
-- ------------------------------
-- Réservation 1: Jean Dupont - Oppenheimer séance 1
DO $$
DECLARE
    v_id_reservation INT;
    v_id_statut_paye INT;
    v_id_place_1 INT;
    v_id_place_2 INT;
    v_prix_1 NUMERIC;
    v_prix_2 NUMERIC;
BEGIN
    -- Récupérer le statut PAYE
    SELECT id INTO v_id_statut_paye FROM statut_ticket WHERE code = 'PAYE';
    
    -- Créer la réservation
    INSERT INTO reservation (id_personne, id_seance, id_statut, montant_total, reference_unique)
    VALUES (2, 1, 2, 0, 'RES-' || LPAD(nextval('reservation_id_seq')::TEXT, 8, '0'))
    RETURNING id INTO v_id_reservation;
    
    -- Trouver 2 places VIP (rangée A)
    SELECT id INTO v_id_place_1 FROM place WHERE id_salle = 3 AND rangee = 'A' AND numero = 5;
    SELECT id INTO v_id_place_2 FROM place WHERE id_salle = 3 AND rangee = 'A' AND numero = 6;
    
    -- Calculer les prix
    v_prix_1 := calculer_prix_ticket(1, 2, 1); -- VIP ADULTE
    v_prix_2 := calculer_prix_ticket(1, 2, 1); -- VIP ADULTE
    
    -- Créer les tickets
    INSERT INTO ticket (id_reservation, id_seance, id_place, id_statut, id_categorie_personne, prix, code_barre)
    VALUES 
        (v_id_reservation, 1, v_id_place_1, v_id_statut_paye, 1, v_prix_1, 'TKT-' || LPAD(nextval('ticket_id_seq')::TEXT, 10, '0')),
        (v_id_reservation, 1, v_id_place_2, v_id_statut_paye, 1, v_prix_2, 'TKT-' || LPAD(nextval('ticket_id_seq')::TEXT, 10, '0'));
    
    -- Mettre à jour le montant total
    UPDATE reservation SET montant_total = v_prix_1 + v_prix_2 WHERE id = v_id_reservation;
END $$;

-- Réservation 2: Marie Martin - Barbie séance promo
DO $$
DECLARE
    v_id_reservation INT;
    v_id_statut_paye INT;
    v_id_place INT;
    v_prix NUMERIC;
BEGIN
    SELECT id INTO v_id_statut_paye FROM statut_ticket WHERE code = 'PAYE';
    
    INSERT INTO reservation (id_personne, id_seance, id_statut, montant_total, reference_unique)
    VALUES (3, 4, 2, 0, 'RES-' || LPAD(nextval('reservation_id_seq')::TEXT, 8, '0'))
    RETURNING id INTO v_id_reservation;
    
    SELECT id INTO v_id_place FROM place WHERE id_salle = 1 AND rangee = 'E' AND numero = 4;
    v_prix := calculer_prix_ticket(4, 1, 2); -- STANDARD ENFANT avec promo
    
    INSERT INTO ticket (id_reservation, id_seance, id_place, id_statut, id_categorie_personne, prix, code_barre)
    VALUES (v_id_reservation, 4, v_id_place, v_id_statut_paye, 2, v_prix, 'TKT-' || LPAD(nextval('ticket_id_seq')::TEXT, 10, '0'));
    
    UPDATE reservation SET montant_total = v_prix WHERE id = v_id_reservation;
END $$;

-- Réactiver les triggers
SET session_replication_role = 'origin';

-- ------------------------------
-- STATISTIQUES
-- ------------------------------
SELECT 'Données de test insérées avec succès!' AS message;
SELECT 'Films: ' || COUNT(*) FROM film;
SELECT 'Salles: ' || COUNT(*) FROM salle;
SELECT 'Places: ' || COUNT(*) FROM place;
SELECT 'Séances: ' || COUNT(*) FROM seance;
SELECT 'Utilisateurs: ' || COUNT(*) FROM personne WHERE role = 'USER';
SELECT 'Réservations: ' || COUNT(*) FROM reservation;
SELECT 'Tickets: ' || COUNT(*) FROM ticket;
