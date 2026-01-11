# Commandes rapides - Cinema Management

## 🗄️ Base de données

### Installation et initialisation
```bash
# Démarrer PostgreSQL
sudo systemctl start postgresql
sudo systemctl status postgresql

# Créer la base et l'utilisateur
sudo -u postgres psql << 'EOF'
CREATE DATABASE cinema_db;
CREATE USER cinema_user WITH ENCRYPTED PASSWORD 'cinema2026';
GRANT ALL PRIVILEGES ON DATABASE cinema_db TO cinema_user;
\q
EOF

# Initialiser le schéma
psql -U cinema_user -d cinema_db -f database/schema.sql

# Charger les données de test
psql -U cinema_user -d cinema_db -f database/test-data.sql
```

### Connexion et requêtes
```bash
# Se connecter
psql -U cinema_user -d cinema_db

# Dans psql:
\dt                                    # Lister les tables
\d+ film                               # Décrire la table film
SELECT * FROM film LIMIT 5;            # Afficher 5 films
SELECT * FROM v_places_disponibles;    # Voir places disponibles
SELECT * FROM v_dashboard_reservations; # Voir dashboard

# Réinitialiser la base
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\q
```

## 🔨 Maven & Build

### Compilation
```bash
# Clean + compile
mvn clean compile

# Tests (si configurés)
mvn test

# Générer le WAR
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Mode verbose
mvn clean package -X
```

### Dépendances
```bash
# Afficher l'arbre des dépendances
mvn dependency:tree

# Télécharger toutes les dépendances
mvn dependency:resolve

# Vérifier les mises à jour
mvn versions:display-dependency-updates
```

## 🚀 Déploiement

### Avec script automatique (recommandé)
```bash
# Rendre exécutable (une seule fois)
chmod +x deploy.sh

# Déployer
./deploy.sh
```

### Manuel
```bash
# Build + déploiement
mvn clean package
sudo cp target/cinema-management.war /opt/tomcat/webapps/

# Redémarrer Tomcat (systemd)
sudo systemctl restart tomcat
sudo systemctl status tomcat

# Redémarrer Tomcat (scripts)
/opt/tomcat/bin/shutdown.sh
/opt/tomcat/bin/startup.sh

# Voir les logs en temps réel
tail -f /opt/tomcat/logs/catalina.out
```

## 🌐 Accès application

```bash
# URL principale
http://localhost:8080/cinema-management/

# API films (JSON)
curl -H "Accept: application/json" http://localhost:8080/cinema-management/films

# Tester avec wget
wget -O- http://localhost:8080/cinema-management/
```

## 📊 Monitoring & Logs

### Logs Tomcat
```bash
# Logs en temps réel
tail -f /opt/tomcat/logs/catalina.out

# Logs application (si configuré)
tail -f /opt/tomcat/logs/cinema-app.log

# Erreurs seulement
grep ERROR /opt/tomcat/logs/catalina.out

# Les 100 dernières lignes
tail -n 100 /opt/tomcat/logs/catalina.out
```

### Pool de connexions (stats)
Les stats HikariCP sont loguées automatiquement. Chercher dans les logs :
```bash
grep "HikariPool" /opt/tomcat/logs/catalina.out
```

## 🧹 Nettoyage

### Nettoyer le build
```bash
# Maven clean
mvn clean

# Supprimer target/
rm -rf target/

# Nettoyer les anciennes sauvegardes WAR
rm /opt/tomcat/backups/cinema-management_*.war
```

### Nettoyer la base de données
```bash
# Supprimer toutes les données (garder le schéma)
psql -U cinema_user -d cinema_db << 'EOF'
TRUNCATE TABLE ticket CASCADE;
TRUNCATE TABLE reservation CASCADE;
TRUNCATE TABLE seance CASCADE;
TRUNCATE TABLE place CASCADE;
TRUNCATE TABLE salle CASCADE;
TRUNCATE TABLE film_genre CASCADE;
TRUNCATE TABLE film CASCADE;
TRUNCATE TABLE personne CASCADE;
EOF

# Réinitialiser complètement
psql -U cinema_user -d cinema_db << 'EOF'
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
\q
EOF
psql -U cinema_user -d cinema_db -f database/schema.sql
psql -U cinema_user -d cinema_db -f database/test-data.sql
```

## 🔍 Debug & Troubleshooting

### Vérifier ports
```bash
# Port 8080 (Tomcat)
sudo lsof -i :8080
sudo netstat -tulpn | grep 8080

# Port 5432 (PostgreSQL)
sudo lsof -i :5432
sudo netstat -tulpn | grep 5432
```

### Tuer un processus bloquant
```bash
# Trouver le PID
sudo lsof -i :8080

# Tuer le processus
sudo kill -9 <PID>
```

### Vérifier connexion DB
```bash
# Test connexion
psql -U cinema_user -d cinema_db -c "SELECT version();"

# Test depuis Java (avec Maven exec)
mvn exec:java -Dexec.mainClass="com.cinema.util.DatabaseConnection"
```

### Erreurs communes

**Erreur : "Connection refused" PostgreSQL**
```bash
sudo systemctl start postgresql
```

**Erreur : "Role does not exist"**
```bash
sudo -u postgres createuser cinema_user
```

**Erreur : "Database does not exist"**
```bash
sudo -u postgres createdb cinema_db
```

**Erreur : Port 8080 déjà utilisé**
```bash
sudo lsof -i :8080
sudo kill -9 <PID>
```

## 🧪 Tests rapides

### Test connexion DB depuis psql
```sql
-- Vérifier les données
SELECT COUNT(*) FROM film;
SELECT COUNT(*) FROM seance;
SELECT COUNT(*) FROM place;

-- Tester fonction disponibilité
SELECT est_place_disponible(1, 1);

-- Tester calcul prix
SELECT calculer_prix_ticket(1, 1, 1);

-- Tester conflit séances
SELECT verifier_conflit_seance(1, now(), now() + interval '2 hours');
```

### Test endpoints avec curl
```bash
# Liste films (HTML)
curl http://localhost:8080/cinema-management/films

# Liste films (JSON)
curl -H "Accept: application/json" \
     http://localhost:8080/cinema-management/films

# Avec authentification (à implémenter)
curl -X POST http://localhost:8080/cinema-management/login \
     -d "email=admin@cinema.mg&password=admin123"
```

## 📦 Sauvegarde & Restauration

### Sauvegarde DB
```bash
# Dump complet
pg_dump -U cinema_user -d cinema_db -F c -f cinema_db_backup_$(date +%Y%m%d).dump

# Dump SQL
pg_dump -U cinema_user -d cinema_db > cinema_db_backup_$(date +%Y%m%d).sql

# Dump données seules (sans schéma)
pg_dump -U cinema_user -d cinema_db --data-only > data_only.sql
```

### Restauration DB
```bash
# Depuis dump custom
pg_restore -U cinema_user -d cinema_db cinema_db_backup_20260108.dump

# Depuis SQL
psql -U cinema_user -d cinema_db < cinema_db_backup_20260108.sql
```

### Sauvegarde WAR
```bash
# Le script deploy.sh sauvegarde automatiquement dans /opt/tomcat/backups/
# Sauvegarde manuelle
cp target/cinema-management.war ~/backups/cinema-management_$(date +%Y%m%d_%H%M%S).war
```

## 🔐 Sécurité

### Changer mot de passe admin
```sql
-- Générer nouveau hash avec BCrypt (depuis Java ou outil en ligne)
-- Puis :
UPDATE personne 
SET mot_de_passe_hash = '$2a$10$NOUVEAU_HASH_ICI' 
WHERE email = 'admin@cinema.mg';
```

### Créer nouvel admin
```sql
INSERT INTO personne (nom_complet, email, telephone, mot_de_passe_hash, role)
VALUES ('Nouvel Admin', 'nouvel.admin@cinema.mg', '0340000001',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
        'ADMIN');
```

## 📈 Performance

### Analyser requêtes lentes
```sql
-- Activer logging requêtes lentes (PostgreSQL)
ALTER DATABASE cinema_db SET log_min_duration_statement = 1000; -- 1 seconde

-- Voir les requêtes actives
SELECT pid, usename, application_name, state, query 
FROM pg_stat_activity 
WHERE datname = 'cinema_db';
```

### Statistiques tables
```sql
-- Statistiques globales
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables 
WHERE schemaname = 'public' 
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Index utilisés
SELECT schemaname, tablename, indexname, idx_scan 
FROM pg_stat_user_indexes 
ORDER BY idx_scan DESC;
```

## 🔄 Git & Version Control

### Commits recommandés
```bash
# Commit initial (déjà fait)
git add .
git commit -m "Initial commit: database schema, Maven config, DAOs"

# Feature branches
git checkout -b feature/authentication
git checkout -b feature/booking-flow
git checkout -b feature/admin-interface

# Push
git push origin main
```

### Ignorer fichiers sensibles
Créer `.gitignore` :
```
target/
*.war
*.class
*.log
src/main/resources/db.properties
.idea/
.vscode/
*.iml
```

---

**💡 Astuce** : Créer des alias bash pour les commandes fréquentes :

```bash
# Ajouter dans ~/.bashrc
alias cinema-build='cd ~/Documents/GitHub/Cinema && mvn clean package'
alias cinema-deploy='cd ~/Documents/GitHub/Cinema && ./deploy.sh'
alias cinema-logs='tail -f /opt/tomcat/logs/catalina.out'
alias cinema-db='psql -U cinema_user -d cinema_db'
```

Puis recharger :
```bash
source ~/.bashrc
```
