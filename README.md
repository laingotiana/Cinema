# Cinema Management System 🎬

**Projet S3-S5 Baovola** - Système de gestion de salle de cinéma

Application web Java EE pour la gestion complète d'un cinéma : billetterie, réservations, programmation des séances, gestion des salles et reporting.

## 📋 Table des matières

- [Technologies utilisées](#-technologies-utilisées)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Déploiement](#-déploiement)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Base de données](#-base-de-données)
- [Tests](#-tests)
- [Contribution](#-contribution)

## 🛠 Technologies utilisées

### Backend
- **Java 17** - Langage de programmation
- **Jakarta Servlet 5.0** - Gestion des requêtes HTTP
- **PostgreSQL 15+** - Base de données relationnelle
- **HikariCP** - Pool de connexions performant
- **BCrypt** - Hashing sécurisé des mots de passe

### Frontend
- **JSP (JavaServer Pages)** - Templating côté serveur
- **JSTL** - Bibliothèque de tags JSP
- **Bootstrap 5** - Framework CSS responsive
- **JavaScript (Vanilla)** - Interactivité côté client

### Build & Déploiement
- **Maven 3.8+** - Gestionnaire de dépendances et build
- **Apache Tomcat 10** - Serveur d'applications
- **Bash** - Scripts de déploiement

### Librairies complémentaires
- **iText 7** - Génération de tickets PDF
- **ZXing** - Génération de QR codes
- **Gson** - Parsing/génération JSON
- **SLF4J + Logback** - Logging

## ✨ Fonctionnalités

### Espace Public (Clients)
- 🎥 **Catalogue de films** - Recherche par titre, genre, date
- 🎟️ **Réservation de places** - Sélection visuelle des sièges
- 📅 **Programmation** - Consultation des séances disponibles
- 💳 **Paiement simulé** - Validation des réservations
- 🎫 **Génération de tickets** - PDF téléchargeable avec QR code
- 👤 **Espace personnel** - Historique des réservations

### Espace Administration
- 🎬 **Gestion des films** - CRUD complet (titre, description, durée, genres, affiche)
- 🏛️ **Gestion des salles** - Configuration des salles et plan de sièges (STANDARD/VIP/PMR)
- 🕐 **Planification des séances** - Programmation avec détection de conflits
- 💰 **Gestion tarifaire** - Tarifs par défaut + tarifs promotionnels par séance
- 📊 **Dashboard & Rapports** - Statistiques de vente, taux d'occupation, revenus
- 👥 **Gestion des utilisateurs** - Rôles ADMIN/USER

### Fonctionnalités techniques
- 🔐 **Authentification sécurisée** - Sessions + BCrypt
- 🔒 **Prévention double-réservation** - Transactions SQL atomiques
- ⚡ **Pool de connexions** - Performance optimisée via HikariCP
- 📱 **Design responsive** - Interface adaptée mobile/tablette/desktop
- 🎯 **Tarification flexible** - Système de surcharge de prix par séance
- 📈 **Traçabilité complète** - Historique des statuts (réservations & tickets)

## 🏗 Architecture

```
┌─────────────┐     HTTP      ┌──────────────┐
│   Browser   │ ◄─────────────► │   Tomcat     │
│   (JSP)     │                │  (Servlets)  │
└─────────────┘                └──────┬───────┘
                                      │
                                      │ JDBC
                                      ▼
                               ┌──────────────┐
                               │  PostgreSQL  │
                               │  (Database)  │
                               └──────────────┘
```

### Pattern MVC
- **Model** : Classes Java (DAO, Entities)
- **View** : JSP + JSTL + Bootstrap
- **Controller** : Servlets Jakarta

## 📦 Prérequis

- **JDK 17** ou supérieur
- **Apache Maven 3.8+**
- **PostgreSQL 15+**
- **Apache Tomcat 10**
- **Git** (pour cloner le projet)

### Vérification des prérequis

```bash
# Java
java -version  # doit afficher Java 17+

# Maven
mvn -version

# PostgreSQL
psql --version

# Tomcat
# Vérifier que Tomcat est installé dans /opt/tomcat
```

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/laingotiana/Cinema.git
cd Cinema
```

### 2. Créer la base de données

```bash
# Se connecter à PostgreSQL
sudo -u postgres psql

# Dans psql:
CREATE DATABASE cinema_db;
CREATE USER cinema_user WITH ENCRYPTED PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE cinema_db TO cinema_user;
\q
```

### 3. Initialiser le schéma

```bash
# Exécuter le script de création
psql -U cinema_user -d cinema_db -f database/schema.sql

# (Optionnel) Charger les données de test
psql -U cinema_user -d cinema_db -f database/test-data.sql
```

### 4. Configurer la connexion DB

```bash
# Copier le template de configuration
cp database/db.properties.example src/main/resources/db.properties

# Éditer avec vos paramètres
nano src/main/resources/db.properties
```

Ajustez les valeurs :
```properties
db.host=localhost
db.port=5432
db.name=cinema_db
db.username=cinema_user
db.password=votre_mot_de_passe
```

### 5. Builder le projet

```bash
mvn clean package
```

Le WAR sera généré dans `target/cinema-management.war`.

## ⚙️ Configuration

### Fichiers de configuration

| Fichier | Description |
|---------|-------------|
| `database/db.properties.example` | Template configuration DB |
| `src/main/resources/db.properties` | Configuration DB (à créer) |
| `src/main/webapp/WEB-INF/web.xml` | Descripteur de déploiement |
| `pom.xml` | Dépendances Maven |

### Variables d'environnement (optionnel)

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=cinema_db
export DB_USER=cinema_user
export DB_PASSWORD=votre_mot_de_passe
```

## 🚢 Déploiement

### Déploiement automatique (recommandé)

```bash
# Rendre le script exécutable
chmod +x deploy.sh

# Déployer
./deploy.sh
```

Le script effectue automatiquement :
- ✅ Vérification du WAR
- ✅ Sauvegarde de l'ancienne version
- ✅ Copie du nouveau WAR
- ✅ Redémarrage de Tomcat
- ✅ Nettoyage des anciennes sauvegardes

### Déploiement manuel

```bash
# Copier le WAR
sudo cp target/cinema-management.war /opt/tomcat/webapps/

# Redémarrer Tomcat
sudo systemctl restart tomcat

# Vérifier les logs
tail -f /opt/tomcat/logs/catalina.out
```

### Accès à l'application

Ouvrez votre navigateur :
```
http://localhost:8080/cinema-management/
```

### Comptes par défaut

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@cinema.mg | admin123 |
| User | jean.dupont@email.com | test123 |

⚠️ **IMPORTANT** : Changez ces mots de passe en production !

## 📖 Utilisation

### Workflow client (réservation)

1. **Parcourir le catalogue** de films
2. **Sélectionner une séance** (date, heure, langue)
3. **Choisir les places** sur le plan de salle interactif
4. **Valider la catégorie** (Adulte/Enfant/Senior/Étudiant)
5. **Payer** (simulation)
6. **Télécharger le ticket PDF** avec QR code

### Workflow admin

1. **Se connecter** avec un compte ADMIN
2. **Ajouter un film** (titre, description, genres, durée, affiche)
3. **Créer une séance** (film, salle, date/heure, langue)
4. **Définir des tarifs promotionnels** (optionnel)
5. **Consulter le dashboard** (statistiques de vente)

## 📁 Structure du projet

```
Cinema/
├── database/
│   ├── schema.sql              # Schéma PostgreSQL complet
│   ├── test-data.sql           # Données de test
│   └── db.properties.example   # Template configuration DB
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cinema/
│       │       ├── dao/        # Data Access Objects
│       │       ├── model/      # Entités (Film, Salle, Ticket...)
│       │       ├── service/    # Logique métier
│       │       ├── servlet/    # Contrôleurs HTTP
│       │       └── util/       # Utilitaires (DB, Hash, PDF...)
│       ├── resources/
│       │   └── db.properties   # Config DB (à créer)
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml     # Descripteur webapp
│           ├── css/            # Styles
│           ├── js/             # Scripts frontend
│           ├── images/         # Assets
│           └── *.jsp           # Pages JSP
├── target/                     # Build output
├── pom.xml                     # Configuration Maven
├── deploy.sh                   # Script de déploiement
└── README.md                   # Ce fichier
```

## 🗄️ Base de données

### Modèle conceptuel

```
┌─────────┐       ┌─────────┐       ┌─────────┐
│  Film   │───┬───│ Séance  │───┬───│  Salle  │
└─────────┘   │   └─────────┘   │   └─────────┘
              │         │        │         │
              │         │        │         ▼
              │         │        │   ┌─────────┐
              │         │        └───│  Place  │
              │         │            └─────────┘
              │         ▼                  │
              │   ┌──────────┐            │
              │   │ Réserv.  │◄───────────┘
              │   └──────────┘
              │         │
              │         ▼
              │   ┌─────────┐
              └───│ Ticket  │
                  └─────────┘
```

### Tables principales

- **film** - Catalogue de films
- **genre** / **film_genre** - Genres cinématographiques
- **salle** - Salles de projection
- **place** - Places individuelles (STANDARD/VIP/PMR)
- **seance** - Séances planifiées
- **personne** - Utilisateurs (clients + admins)
- **reservation** - Réservations groupées
- **ticket** - Tickets individuels (1 place = 1 ticket)
- **tarif_defaut** / **tarif_seance** - Grille tarifaire
- **statut_reservation** / **statut_ticket** - États possibles
- **historique_statut_*** - Traçabilité complète

### Fonctions SQL utiles

```sql
-- Vérifier disponibilité d'une place
SELECT est_place_disponible(id_seance, id_place);

-- Calculer prix d'un ticket
SELECT calculer_prix_ticket(id_seance, id_type_place, id_categorie_personne);

-- Vérifier conflit de séances
SELECT verifier_conflit_seance(id_salle, debut, fin);
```

### Vues pré-créées

```sql
-- Places disponibles par séance
SELECT * FROM v_places_disponibles WHERE id_seance = 1;

-- Dashboard réservations
SELECT * FROM v_dashboard_reservations;
```

## 🧪 Tests

### Tests unitaires

```bash
mvn test
```

### Tests d'intégration

```bash
# Avec base de données de test
mvn verify
```

### Tests manuels

1. **Réservation simple** : créer une réservation pour une séance
2. **Double réservation** : tenter de réserver 2× la même place → doit échouer
3. **Conflit de séances** : créer 2 séances qui se chevauchent dans la même salle → doit échouer
4. **Tarif promo** : créer un tarif spécifique pour une séance et vérifier le calcul
5. **Génération ticket** : télécharger un PDF et scanner le QR code

## 📝 TODO / Améliorations futures

- [ ] Intégration paiement réel (Stripe/PayPal)
- [ ] Envoi d'emails de confirmation (JavaMail)
- [ ] API REST pour mobile app
- [ ] Système de notation des films
- [ ] Recommandations personnalisées
- [ ] Multi-cinémas (gestion de plusieurs établissements)
- [ ] Interface caissier pour vente sur place
- [ ] Export PDF des rapports
- [ ] Internationalisation (FR/EN/MG)
- [ ] Tests avec Testcontainers

## 👥 Contribution

Ce projet est développé dans le cadre du cours S3-S5 Baovola.

### Auteurs

- **Équipe Baovola** - Développement initial
- **Repository** : [laingotiana/Cinema](https://github.com/laingotiana/Cinema)

### Workflow de contribution

```bash
# Fork le projet
git checkout -b feature/ma-fonctionnalite
git commit -m "Ajout de ma fonctionnalité"
git push origin feature/ma-fonctionnalite
# Créer une Pull Request
```

## 📄 Licence

Projet éducatif - Tous droits réservés © 2026

## 📞 Support

Pour toute question ou problème :
- Ouvrir une issue GitHub
- Consulter la documentation dans `/docs`
- Contacter l'équipe de développement

---

**Fait avec ❤️ par l'équipe Baovola** - Janvier 2026
