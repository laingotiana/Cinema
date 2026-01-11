-- ------------------------------
-- GESTION CINEMA - VERSION FINALE
-- Base de données: PostgreSQL
-- Date: 8 janvier 2026
-- ------------------------------

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS historique_statut_ticket CASCADE;
DROP TABLE IF EXISTS historique_statut_reservation CASCADE;
DROP TABLE IF EXISTS tarif_seance CASCADE;
DROP TABLE IF EXISTS tarif_defaut CASCADE;
DROP TABLE IF EXISTS ticket CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS statut_ticket CASCADE;
DROP TABLE IF EXISTS statut_reservation CASCADE;
DROP TABLE IF EXISTS seance CASCADE;
DROP TABLE IF EXISTS place CASCADE;
DROP TABLE IF EXISTS salle CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS personne CASCADE;
DROP TABLE IF EXISTS categorie_personne CASCADE;
DROP TABLE IF EXISTS type_place CASCADE;

-- ------------------------------
-- TYPE DE PLACE
-- ------------------------------
CREATE TABLE type_place (
    id SERIAL PRIMARY KEY,
    libelle TEXT NOT NULL -- STANDARD, VIP, PMR
);

-- ------------------------------
-- CATEGORIE PERSONNE
-- ------------------------------
CREATE TABLE categorie_personne (
    id SERIAL PRIMARY KEY,
    libelle TEXT NOT NULL -- ADULTE, ENFANT, SENIOR...
);

-- ------------------------------
-- FILMS & GENRES
-- ------------------------------
CREATE TABLE film (
    id SERIAL PRIMARY KEY,
    titre TEXT NOT NULL,
    description TEXT,
    duree_minutes INT,
    date_sortie DATE,
    age_min INT DEFAULT 0, -- age minimum conseillé
    langue_originale TEXT, -- langue du film
    affiche_url TEXT, -- URL de l'affiche du film
    cree_le TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE genre (
    id SERIAL PRIMARY KEY,
    libelle TEXT UNIQUE NOT NULL
);

CREATE TABLE film_genre (
    id_film INT REFERENCES film(id) ON DELETE CASCADE,
    id_genre INT REFERENCES genre(id) ON DELETE CASCADE,
    PRIMARY KEY (id_film, id_genre)
);

-- ------------------------------
-- SALLES & PLACES
-- ------------------------------
CREATE TABLE salle (
    id SERIAL PRIMARY KEY,
    nom TEXT NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    cree_le TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE place (
    id SERIAL PRIMARY KEY,
    id_salle INT REFERENCES salle(id) ON DELETE CASCADE,
    rangee TEXT,
    numero INT,
    code_place TEXT, -- ex: A1, B5, etc.
    id_type_place INT REFERENCES type_place(id),
    cree_le TIMESTAMPTZ DEFAULT now(),
    UNIQUE (id_salle, rangee, numero)
);

-- Index pour recherche rapide des places par salle
CREATE INDEX idx_place_salle ON place(id_salle);

-- ------------------------------
-- SEANCES
-- ------------------------------
CREATE TABLE seance (
    id SERIAL PRIMARY KEY,
    id_film INT REFERENCES film(id),
    id_salle INT REFERENCES salle(id),
    debut TIMESTAMPTZ NOT NULL,
    fin TIMESTAMPTZ,
    langue TEXT, -- Langue de la séance (VF, VO, etc.)
    cree_le TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_seance_salle_debut ON seance(id_salle, debut);
CREATE INDEX idx_seance_film ON seance(id_film);
CREATE INDEX idx_seance_debut ON seance(debut);

-- ------------------------------
-- PERSONNES
-- ------------------------------
CREATE TABLE personne (
    id SERIAL PRIMARY KEY,
    nom_complet TEXT,
    email TEXT UNIQUE,
    telephone TEXT,
    mot_de_passe_hash TEXT, -- Hash du mot de passe pour authentification
    role TEXT DEFAULT 'USER', -- USER ou ADMIN
    cree_le TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_personne_email ON personne(email);

-- ------------------------------
-- STATUTS RESERVATION
-- ------------------------------
CREATE TABLE statut_reservation (
    id SERIAL PRIMARY KEY,
    code TEXT UNIQUE NOT NULL, -- CREEE, EN_ATTENTE, PAYEE, CONFIRMEE, ANNULEE, EXPIREE
    libelle TEXT NOT NULL,
    est_final BOOLEAN DEFAULT false
);

-- ------------------------------
-- RESERVATIONS
-- ------------------------------
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    id_personne INT REFERENCES personne(id) NULL, -- nullable pour vente sur place
    id_seance INT REFERENCES seance(id),
    id_statut INT REFERENCES statut_reservation(id),
    montant_total NUMERIC(8,2) DEFAULT 0,
    reference_unique TEXT UNIQUE, -- Référence unique pour retrouver la réservation
    cree_le TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_reservation_personne ON reservation(id_personne);
CREATE INDEX idx_reservation_seance ON reservation(id_seance);
CREATE INDEX idx_reservation_reference ON reservation(reference_unique);

-- ------------------------------
-- HISTORIQUE STATUT RESERVATION
-- ------------------------------
CREATE TABLE historique_statut_reservation (
    id SERIAL PRIMARY KEY,
    id_reservation INT REFERENCES reservation(id) ON DELETE CASCADE,
    id_statut INT REFERENCES statut_reservation(id),
    date_changement TIMESTAMPTZ DEFAULT now(),
    change_par INT REFERENCES personne(id),
    commentaire TEXT
);

CREATE INDEX idx_historique_res_reservation ON historique_statut_reservation(id_reservation);

-- ------------------------------
-- STATUTS TICKET
-- ------------------------------
CREATE TABLE statut_ticket (
    id SERIAL PRIMARY KEY,
    code TEXT UNIQUE NOT NULL, -- RESERVE, PAYE, ANNULE, UTILISE, REMBOURSE
    libelle TEXT NOT NULL,
    est_final BOOLEAN DEFAULT false
);

-- ------------------------------
-- TICKETS
-- ------------------------------
CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    id_reservation INT REFERENCES reservation(id) NULL, -- nullable pour ticket sans réservation
    id_seance INT REFERENCES seance(id),
    id_place INT REFERENCES place(id),
    id_statut INT REFERENCES statut_ticket(id),
    id_categorie_personne INT REFERENCES categorie_personne(id), -- adulte/enfant
    prix NUMERIC(8,2) NOT NULL,
    code_barre TEXT UNIQUE, -- Code-barres ou QR code pour validation
    cree_le TIMESTAMPTZ DEFAULT now(),
    UNIQUE (id_seance, id_place) -- Une place ne peut être réservée qu'une fois par séance
);

CREATE INDEX idx_ticket_reservation ON ticket(id_reservation);
CREATE INDEX idx_ticket_seance ON ticket(id_seance);
CREATE INDEX idx_ticket_place ON ticket(id_place);
CREATE INDEX idx_ticket_code_barre ON ticket(code_barre);

-- ------------------------------
-- HISTORIQUE STATUT TICKET
-- ------------------------------
CREATE TABLE historique_statut_ticket (
    id SERIAL PRIMARY KEY,
    id_ticket INT REFERENCES ticket(id) ON DELETE CASCADE,
    id_statut INT REFERENCES statut_ticket(id),
    date_changement TIMESTAMPTZ DEFAULT now(),
    change_par INT REFERENCES personne(id),
    commentaire TEXT
);

CREATE INDEX idx_historique_ticket_ticket ON historique_statut_ticket(id_ticket);

-- ------------------------------
-- TARIF PAR DEFAUT
-- ------------------------------
CREATE TABLE tarif_defaut (
    id SERIAL PRIMARY KEY,
    id_type_place INT REFERENCES type_place(id),
    id_categorie_personne INT REFERENCES categorie_personne(id),
    prix NUMERIC(6,2) NOT NULL,
    UNIQUE (id_type_place, id_categorie_personne)
);

-- ------------------------------
-- TARIF SPECIFIQUE PAR SEANCE (OPTIONNEL)
-- ------------------------------
CREATE TABLE tarif_seance (
    id SERIAL PRIMARY KEY,
    id_seance INT REFERENCES seance(id) ON DELETE CASCADE,
    id_type_place INT REFERENCES type_place(id),
    id_categorie_personne INT REFERENCES categorie_personne(id),
    prix NUMERIC(6,2) NOT NULL,
    UNIQUE (id_seance, id_type_place, id_categorie_personne)
);

CREATE INDEX idx_tarif_seance_seance ON tarif_seance(id_seance);

-- ------------------------------
-- DONNEES INITIALES - STATUTS
-- ------------------------------
INSERT INTO statut_reservation (code, libelle, est_final) VALUES
    ('CREEE', 'Créée', false),
    ('EN_ATTENTE', 'En attente de paiement', false),
    ('PAYEE', 'Payée', false),
    ('CONFIRMEE', 'Confirmée', false),
    ('ANNULEE', 'Annulée', true),
    ('EXPIREE', 'Expirée', true);

INSERT INTO statut_ticket (code, libelle, est_final) VALUES
    ('RESERVE', 'Réservé', false),
    ('PAYE', 'Payé', false),
    ('ANNULE', 'Annulé', true),
    ('UTILISE', 'Utilisé', true),
    ('REMBOURSE', 'Remboursé', true);

-- ------------------------------
-- DONNEES INITIALES - TYPES DE PLACE
-- ------------------------------
INSERT INTO type_place (libelle) VALUES
    ('STANDARD'),
    ('VIP'),
    ('PMR'); -- Personne à Mobilité Réduite

-- ------------------------------
-- DONNEES INITIALES - CATEGORIES PERSONNE
-- ------------------------------
INSERT INTO categorie_personne (libelle) VALUES
    ('ADULTE'),
    ('ENFANT'),
    ('SENIOR'),
    ('ETUDIANT');

-- ------------------------------
-- DONNEES INITIALES - GENRES
-- ------------------------------
INSERT INTO genre (libelle) VALUES
    ('Action'),
    ('Comédie'),
    ('Drame'),
    ('Horreur'),
    ('Science-Fiction'),
    ('Romance'),
    ('Thriller'),
    ('Animation'),
    ('Documentaire'),
    ('Aventure');

-- ------------------------------
-- TARIFS PAR DEFAUT
-- ------------------------------
-- STANDARD
INSERT INTO tarif_defaut (id_type_place, id_categorie_personne, prix) VALUES
    (1, 1, 10.00), -- STANDARD ADULTE
    (1, 2, 6.00),  -- STANDARD ENFANT
    (1, 3, 8.00),  -- STANDARD SENIOR
    (1, 4, 8.50);  -- STANDARD ETUDIANT

-- VIP
INSERT INTO tarif_defaut (id_type_place, id_categorie_personne, prix) VALUES
    (2, 1, 15.00), -- VIP ADULTE
    (2, 2, 10.00), -- VIP ENFANT
    (2, 3, 12.00), -- VIP SENIOR
    (2, 4, 12.50); -- VIP ETUDIANT

-- PMR (même prix que STANDARD)
INSERT INTO tarif_defaut (id_type_place, id_categorie_personne, prix) VALUES
    (3, 1, 10.00), -- PMR ADULTE
    (3, 2, 6.00),  -- PMR ENFANT
    (3, 3, 8.00),  -- PMR SENIOR
    (3, 4, 8.50);  -- PMR ETUDIANT

-- ------------------------------
-- COMPTE ADMIN PAR DEFAUT
-- Mot de passe: admin123 (hash bcrypt)
-- IMPORTANT: Changer ce mot de passe en production!
-- ------------------------------
INSERT INTO personne (nom_complet, email, telephone, mot_de_passe_hash, role) VALUES
    ('Administrateur Système', 'admin@cinema.mg', '0340000000', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
     'ADMIN');

-- ------------------------------
-- FONCTIONS UTILITAIRES
-- ------------------------------

-- Fonction pour vérifier la disponibilité d'une place pour une séance
CREATE OR REPLACE FUNCTION est_place_disponible(
    p_id_seance INT,
    p_id_place INT
) RETURNS BOOLEAN AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM ticket
    WHERE id_seance = p_id_seance
      AND id_place = p_id_place
      AND id_statut IN (
          SELECT id FROM statut_ticket WHERE code IN ('RESERVE', 'PAYE', 'UTILISE')
      );
    
    RETURN v_count = 0;
END;
$$ LANGUAGE plpgsql;

-- Fonction pour calculer le prix d'un ticket
CREATE OR REPLACE FUNCTION calculer_prix_ticket(
    p_id_seance INT,
    p_id_type_place INT,
    p_id_categorie_personne INT
) RETURNS NUMERIC AS $$
DECLARE
    v_prix NUMERIC;
BEGIN
    -- Chercher d'abord dans tarif_seance (surcharge)
    SELECT prix INTO v_prix
    FROM tarif_seance
    WHERE id_seance = p_id_seance
      AND id_type_place = p_id_type_place
      AND id_categorie_personne = p_id_categorie_personne;
    
    -- Si pas trouvé, utiliser le tarif par défaut
    IF v_prix IS NULL THEN
        SELECT prix INTO v_prix
        FROM tarif_defaut
        WHERE id_type_place = p_id_type_place
          AND id_categorie_personne = p_id_categorie_personne;
    END IF;
    
    RETURN COALESCE(v_prix, 0);
END;
$$ LANGUAGE plpgsql;

-- Fonction pour vérifier les conflits de séances dans une salle
CREATE OR REPLACE FUNCTION verifier_conflit_seance(
    p_id_salle INT,
    p_debut TIMESTAMPTZ,
    p_fin TIMESTAMPTZ,
    p_id_seance_exclu INT DEFAULT NULL
) RETURNS BOOLEAN AS $$
DECLARE
    v_count INT;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM seance
    WHERE id_salle = p_id_salle
      AND (p_id_seance_exclu IS NULL OR id != p_id_seance_exclu)
      AND (
          (debut <= p_debut AND fin > p_debut) OR
          (debut < p_fin AND fin >= p_fin) OR
          (debut >= p_debut AND fin <= p_fin)
      );
    
    RETURN v_count > 0;
END;
$$ LANGUAGE plpgsql;

-- ------------------------------
-- TRIGGERS
-- ------------------------------

-- Trigger pour calculer automatiquement la fin de séance
CREATE OR REPLACE FUNCTION calculer_fin_seance() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.fin IS NULL THEN
        -- compute fin from NEW.debut and the film duration + 30 minutes buffer
        SELECT (NEW.debut + (duree_minutes || ' minutes')::INTERVAL + INTERVAL '30 minutes')
        INTO NEW.fin
        FROM film
        WHERE id = NEW.id_film;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_calculer_fin_seance
    BEFORE INSERT OR UPDATE ON seance
    FOR EACH ROW
    EXECUTE FUNCTION calculer_fin_seance();

-- Trigger pour insérer automatiquement l'historique de statut de réservation
CREATE OR REPLACE FUNCTION inserer_historique_reservation() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND OLD.id_statut IS DISTINCT FROM NEW.id_statut) THEN
        INSERT INTO historique_statut_reservation (id_reservation, id_statut, date_changement)
        VALUES (NEW.id, NEW.id_statut, now());
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_historique_reservation
    AFTER INSERT OR UPDATE ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION inserer_historique_reservation();

-- Trigger pour insérer automatiquement l'historique de statut de ticket
CREATE OR REPLACE FUNCTION inserer_historique_ticket() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND OLD.id_statut IS DISTINCT FROM NEW.id_statut) THEN
        INSERT INTO historique_statut_ticket (id_ticket, id_statut, date_changement)
        VALUES (NEW.id, NEW.id_statut, now());
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_historique_ticket
    AFTER INSERT OR UPDATE ON ticket
    FOR EACH ROW
    EXECUTE FUNCTION inserer_historique_ticket();

-- ------------------------------
-- VUES UTILES
-- ------------------------------

-- Vue pour voir les places disponibles par séance
CREATE OR REPLACE VIEW v_places_disponibles AS
SELECT 
    s.id AS id_seance,
    s.debut,
    f.titre AS film,
    sal.nom AS salle,
    p.id AS id_place,
    p.code_place,
    p.rangee,
    p.numero,
    tp.libelle AS type_place,
    CASE 
        WHEN t.id IS NULL THEN true
        WHEN st.code IN ('ANNULE', 'REMBOURSE') THEN true
        ELSE false
    END AS disponible
FROM seance s
JOIN film f ON s.id_film = f.id
JOIN salle sal ON s.id_salle = sal.id
JOIN place p ON p.id_salle = sal.id
LEFT JOIN type_place tp ON p.id_type_place = tp.id
LEFT JOIN ticket t ON t.id_seance = s.id AND t.id_place = p.id
LEFT JOIN statut_ticket st ON t.id_statut = st.id
ORDER BY s.debut, sal.nom, p.rangee, p.numero;

-- Vue pour le tableau de bord des réservations
CREATE OR REPLACE VIEW v_dashboard_reservations AS
SELECT 
    DATE(s.debut) AS date_seance,
    f.titre AS film,
    sal.nom AS salle,
    COUNT(DISTINCT r.id) AS nb_reservations,
    COUNT(t.id) AS nb_tickets,
    SUM(t.prix) AS revenu_total,
    sal.capacite,
    ROUND((COUNT(t.id)::NUMERIC / sal.capacite) * 100, 2) AS taux_occupation
FROM seance s
JOIN film f ON s.id_film = f.id
JOIN salle sal ON s.id_salle = sal.id
LEFT JOIN reservation r ON r.id_seance = s.id
LEFT JOIN ticket t ON t.id_reservation = r.id
LEFT JOIN statut_ticket st ON t.id_statut = st.id
WHERE st.code NOT IN ('ANNULE', 'REMBOURSE') OR st.code IS NULL
GROUP BY DATE(s.debut), f.titre, sal.nom, sal.capacite
ORDER BY date_seance DESC, film;

-- ------------------------------
-- COMMENTAIRES SUR LES TABLES
-- ------------------------------
COMMENT ON TABLE type_place IS 'Types de places disponibles dans le cinéma (STANDARD, VIP, PMR)';
COMMENT ON TABLE categorie_personne IS 'Catégories de spectateurs pour la tarification différenciée';
COMMENT ON TABLE film IS 'Catalogue des films';
COMMENT ON TABLE genre IS 'Genres cinématographiques';
COMMENT ON TABLE salle IS 'Salles de projection du cinéma';
COMMENT ON TABLE place IS 'Places individuelles dans chaque salle';
COMMENT ON TABLE seance IS 'Projections planifiées';
COMMENT ON TABLE personne IS 'Utilisateurs du système (clients et administrateurs)';
COMMENT ON TABLE reservation IS 'Réservations de places pour une séance';
COMMENT ON TABLE ticket IS 'Tickets individuels (1 ticket = 1 place pour 1 séance)';
COMMENT ON TABLE tarif_defaut IS 'Grille tarifaire par défaut (type_place × catégorie_personne)';
COMMENT ON TABLE tarif_seance IS 'Surcharge tarifaire pour séances spécifiques';

-- ------------------------------
-- FIN DU SCRIPT
-- ------------------------------
