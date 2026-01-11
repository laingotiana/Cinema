# Guide d'installation rapide 🚀

## Installation en 10 minutes

### 1️⃣ Prérequis (5 min)

```bash
# Vérifier Java 17+
java -version

# Vérifier Maven
mvn -version

# Vérifier PostgreSQL
psql --version
```

Si manquant, installer :
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk maven postgresql postgresql-contrib

# Fedora/RHEL
sudo dnf install java-17-openjdk maven postgresql-server
```

### 2️⃣ Base de données (2 min)

```bash
# Démarrer PostgreSQL
sudo systemctl start postgresql

# Créer la base
sudo -u postgres psql << EOF
CREATE DATABASE cinema_db;
CREATE USER cinema_user WITH ENCRYPTED PASSWORD 'cinema2026';
GRANT ALL PRIVILEGES ON DATABASE cinema_db TO cinema_user;
\q
EOF

# Initialiser le schéma
psql -U cinema_user -d cinema_db -f database/schema.sql
psql -U cinema_user -d cinema_db -f database/test-data.sql
```

### 3️⃣ Configuration (1 min)

```bash
# Créer le fichier de configuration
mkdir -p src/main/resources
cat > src/main/resources/db.properties << EOF
db.host=localhost
db.port=5432
db.name=cinema_db
db.username=cinema_user
db.password=cinema2026
EOF
```

### 4️⃣ Build & Déploiement (2 min)

```bash
# Builder le projet
mvn clean package

# Déployer sur Tomcat
chmod +x deploy.sh
./deploy.sh
```

### 5️⃣ Accès ✅

Ouvrir dans le navigateur :
```
http://localhost:8080/cinema-management/
```

**Compte admin** :
- Email : `admin@cinema.mg`
- Mot de passe : `admin123`

---

## Dépannage rapide

### Erreur : "Connection refused" PostgreSQL
```bash
sudo systemctl status postgresql
sudo systemctl start postgresql
```

### Erreur : Port 8080 déjà utilisé
```bash
# Trouver le processus
sudo lsof -i :8080
# Tuer le processus
sudo kill -9 <PID>
```

### Erreur : WAR non déployé
```bash
# Vérifier les logs Tomcat
tail -f /opt/tomcat/logs/catalina.out
```

### Réinitialiser la base de données
```bash
psql -U cinema_user -d cinema_db << EOF
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
EOF
psql -U cinema_user -d cinema_db -f database/schema.sql
psql -U cinema_user -d cinema_db -f database/test-data.sql
```

---

## Commandes utiles

```bash
# Rebuild rapide
mvn clean package -DskipTests

# Redémarrer Tomcat
sudo systemctl restart tomcat

# Voir les logs en direct
tail -f /opt/tomcat/logs/catalina.out

# Nettoyer complètement
mvn clean
rm -rf target/
```
