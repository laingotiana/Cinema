# Document de conception - Cinema Management System

## 📐 Vue d'ensemble

### Objectif
Créer un système de gestion de cinéma permettant :
- Aux **clients** de réserver des places en ligne
- Aux **administrateurs** de gérer films, salles, séances et tarifs
- À l'**établissement** de suivre les ventes et l'occupation

### Principes de conception
1. **Simplicité** : Interface intuitive, workflow clair
2. **Fiabilité** : Pas de double-réservation, transactions atomiques
3. **Flexibilité** : Tarifs modulables, types de places variés
4. **Traçabilité** : Historique complet des statuts
5. **Performance** : Pool de connexions, requêtes optimisées

---

## 🏗 Architecture technique

### Stack technologique

| Couche | Technologies |
|--------|--------------|
| **Présentation** | JSP, JSTL, Bootstrap 5, JavaScript |
| **Contrôleur** | Jakarta Servlet 5.0 |
| **Métier** | Java 17, Services métier |
| **Persistance** | JDBC, DAO Pattern, PostgreSQL |
| **Infrastructure** | Tomcat 10, HikariCP, Maven |

### Pattern architectural : MVC

```
┌───────────────────────────────────────────────────────┐
│                    PRESENTATION                       │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │ index.  │  │ films.  │  │ booking.│  │ admin.  │ │
│  │  jsp    │  │  jsp    │  │  jsp    │  │  jsp    │ │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘ │
└───────┼───────────┼────────────┼────────────┼───────┘
        │           │            │            │
        │  HTTP     │            │            │
        ▼           ▼            ▼            ▼
┌───────────────────────────────────────────────────────┐
│                    CONTROLEUR                         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │ Home    │  │ Films   │  │ Booking │  │ Admin   │ │
│  │ Servlet │  │ Servlet │  │ Servlet │  │ Servlet │ │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘ │
└───────┼───────────┼────────────┼────────────┼───────┘
        │           │            │            │
        │  Appels   │            │            │
        ▼           ▼            ▼            ▼
┌───────────────────────────────────────────────────────┐
│                    SERVICES METIER                    │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐ │
│  │ FilmService │  │BookingService│  │PricingServ. │ │
│  └──────┬──────┘  └──────┬───────┘  └──────┬──────┘ │
└─────────┼────────────────┼──────────────────┼────────┘
          │                │                  │
          │  DAO calls     │                  │
          ▼                ▼                  ▼
┌───────────────────────────────────────────────────────┐
│                    PERSISTENCE (DAO)                  │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │ FilmDAO │  │SeanceDAO│  │TicketDAO│  │ UserDAO │ │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘ │
└───────┼───────────┼────────────┼────────────┼───────┘
        │           │            │            │
        │  JDBC     │            │            │
        ▼           ▼            ▼            ▼
┌───────────────────────────────────────────────────────┐
│              PostgreSQL Database                      │
└───────────────────────────────────────────────────────┘
```

---

## 🗄️ Conception de la base de données

### Modèle conceptuel (diagramme ER simplifié)

```
FILM (1,N)──────┬──────(1,N) SEANCE
  │             │               │
  │             │               │ (1,1)
  │             │               ▼
  │             │           SALLE (1,1)
  │             │               │
  │             │               │ (1,N)
  │             │               ▼
  │             │           PLACE
  │             │               │
  │             │               │ (0,N)
  │             │               ▼
  │             └──────────► TICKET (0,1)
  │                             │
  │                             │ (0,N)
  │                             ▼
  └────────────────────────► RESERVATION (0,1)
                                │
                                │ (0,1)
                                ▼
                            PERSONNE
```

### Tables principales et leurs responsabilités

| Table | Responsabilité | Cardinalité |
|-------|---------------|-------------|
| **film** | Catalogue de films | - |
| **genre** | Liste des genres | - |
| **film_genre** | Association N:N films↔genres | N:N |
| **salle** | Salles de projection | - |
| **place** | Places individuelles | N places → 1 salle |
| **type_place** | Types (STANDARD/VIP/PMR) | 1 type → N places |
| **seance** | Projections planifiées | 1 séance → 1 film, 1 salle |
| **personne** | Utilisateurs (clients/admins) | - |
| **categorie_personne** | Catégories tarifaires | - |
| **reservation** | Réservations groupées | 1 réservation → N tickets |
| **ticket** | Billets individuels | 1 ticket → 1 place, 1 séance |
| **tarif_defaut** | Grille tarifaire de base | - |
| **tarif_seance** | Surcharges promotionnelles | N tarifs → 1 séance |
| **statut_reservation** | États possibles (CREEE, PAYEE...) | - |
| **statut_ticket** | États possibles (RESERVE, PAYE...) | - |
| **historique_statut_*** | Traçabilité des changements | N historiques → 1 entité |

### Règles métier implémentées en SQL

#### 1. Unicité place/séance (contrainte)
```sql
UNIQUE (id_seance, id_place) sur ticket
```
→ Garantit qu'une place ne peut être réservée 2× pour la même séance.

#### 2. Calcul automatique fin de séance (trigger)
```sql
CREATE TRIGGER trigger_calculer_fin_seance
    BEFORE INSERT OR UPDATE ON seance
```
→ Calcule automatiquement `fin = debut + duree_film + 30min`.

#### 3. Historique automatique (trigger)
```sql
CREATE TRIGGER trigger_historique_reservation
CREATE TRIGGER trigger_historique_ticket
```
→ Insère automatiquement un enregistrement d'historique à chaque changement de statut.

#### 4. Fonctions métier

**est_place_disponible(id_seance, id_place)**
- Retourne `true` si la place est libre
- Utilisé avant insertion de ticket

**calculer_prix_ticket(id_seance, id_type_place, id_categorie_personne)**
- Cherche d'abord dans `tarif_seance` (surcharge)
- Sinon prend `tarif_defaut`
- Retourne le prix calculé

**verifier_conflit_seance(id_salle, debut, fin)**
- Détecte si une séance chevauche une existante
- Retourne `true` si conflit

---

## 🎯 Cas d'utilisation principaux

### CU1 : Réserver des places

**Acteur** : Client (USER)

**Préconditions** : 
- Utilisateur connecté
- Séance disponible avec places libres

**Scénario nominal** :
1. Client parcourt le catalogue de films
2. Client sélectionne un film → affichage des séances
3. Client choisit une séance → affichage du plan de salle
4. Client sélectionne 1+ places (visuellement)
5. Client choisit catégorie(s) (Adulte/Enfant/Senior)
6. Système calcule le prix total
7. Client confirme → paiement simulé
8. Système crée :
   - 1 `reservation` (statut PAYEE)
   - N `ticket` (statut PAYE, 1 par place)
   - Historiques associés
9. Système génère ticket PDF avec QR code
10. Client télécharge/imprime le ticket

**Scénario alternatif 4a** : Place déjà réservée
- Système désactive la place dans l'UI
- Client ne peut pas la sélectionner

**Scénario alternatif 7a** : Paiement échoué
- Système annule la réservation
- Tickets passent en statut ANNULE

**Postconditions** :
- Place(s) marquée(s) comme prises
- Ticket(s) généré(s) avec référence unique

### CU2 : Planifier une séance

**Acteur** : Administrateur (ADMIN)

**Préconditions** :
- Utilisateur connecté avec rôle ADMIN
- Film existant, salle existante

**Scénario nominal** :
1. Admin accède à "Gestion des séances"
2. Admin clique "Nouvelle séance"
3. Admin sélectionne :
   - Film
   - Salle
   - Date/heure de début
   - Langue (VF/VO)
4. Système vérifie les conflits (fonction `verifier_conflit_seance`)
5. Système calcule automatiquement l'heure de fin
6. Admin valide
7. Système crée la séance
8. (Optionnel) Admin définit des tarifs promotionnels spécifiques

**Scénario alternatif 4a** : Conflit détecté
- Système affiche message d'erreur
- Affiche la séance en conflit
- Invite à choisir un autre créneau

**Postconditions** :
- Séance créée et visible publiquement
- Places de la salle associées (disponibles)

### CU3 : Consulter le dashboard

**Acteur** : Administrateur

**Préconditions** : Utilisateur connecté avec rôle ADMIN

**Scénario nominal** :
1. Admin accède au dashboard
2. Système affiche :
   - Nombre de réservations par jour
   - Taux d'occupation par salle/séance
   - Revenu total (période sélectionnable)
   - Top 5 films les plus vus
   - Graphiques d'évolution
3. Admin peut filtrer par :
   - Date (aujourd'hui, semaine, mois)
   - Salle
   - Film
4. Admin peut exporter en CSV

**Postconditions** : Aucune (consultation seule)

---

## 🔒 Sécurité

### Authentification
- **Hashing des mots de passe** : BCrypt (factor 10)
- **Sessions** : Jakarta HttpSession
- **Logout** : Invalidation session + redirection

### Autorisation
- **Rôles** : ADMIN, USER
- **Filtre** : `AuthFilter` vérifie le rôle avant chaque requête admin
- **Pages protégées** : `/admin/*` nécessite rôle ADMIN

### Protection contre les attaques

| Attaque | Protection |
|---------|-----------|
| **SQL Injection** | PreparedStatement (requêtes paramétrées) |
| **XSS** | Échappement JSTL (`<c:out>`) |
| **CSRF** | Token CSRF (à implémenter) |
| **Double réservation** | Transaction SQL + contrainte UNIQUE |
| **Session hijacking** | HTTPS (production) + `HttpOnly` cookies |

---

## ⚡ Performance

### Optimisations DB

1. **Pool de connexions** : HikariCP (10 connexions max)
2. **Index** :
   - `idx_seance_salle_debut` (recherche séances)
   - `idx_ticket_seance` (places occupées)
   - `idx_reservation_personne` (historique client)
3. **Transactions** : Minimiser la durée (commit rapide)
4. **Requêtes préparées** : Réutilisation du plan d'exécution

### Optimisations frontend

1. **CSS/JS minifiés** : En production
2. **Images optimisées** : Affiches films (WebP + CDN)
3. **Pagination** : Catalogue films, historique réservations
4. **Cache** : Headers HTTP (affiches, CSS, JS)

---

## 🧪 Stratégie de tests

### Tests unitaires (JUnit)
- **DAO** : CRUD sur chaque table
- **Services** : Logique métier (calcul prix, vérification conflit)
- **Utilitaires** : Hashing, génération QR, PDF

### Tests d'intégration
- **Workflow réservation** : Bout-en-bout (Selenium optionnel)
- **Transactions** : Rollback en cas d'erreur
- **Concurrence** : Double-réservation simultanée → doit échouer

### Tests manuels
- **Scénarios utilisateur** : Parcours complets
- **Edge cases** : Salle pleine, séance passée, données invalides

### Couverture cible
- DAO : 80%+
- Services : 90%+
- Global : 70%+

---

## 📦 Déploiement

### Environnements

| Env | Base de données | URL | Objectif |
|-----|-----------------|-----|----------|
| **Dev** | cinema_db_dev | localhost:8080 | Développement |
| **Test** | cinema_db_test | test.cinema.local | Tests automatisés |
| **Prod** | cinema_db_prod | cinema.example.com | Production |

### Pipeline CI/CD (futur)

```
1. git push → GitHub
2. GitHub Actions déclenché
3. mvn test (tests unitaires)
4. mvn verify (tests d'intégration)
5. mvn package → WAR
6. Deploy sur serveur test
7. Tests smoke
8. (Manuel) Approval
9. Deploy sur prod
```

### Checklist pré-déploiement

- [ ] Changer mot de passe admin par défaut
- [ ] Configurer HTTPS (certificat SSL)
- [ ] Activer logs en mode INFO (pas DEBUG)
- [ ] Sauvegarder la base de données
- [ ] Tester le rollback (script de backup)
- [ ] Configurer monitoring (logs Tomcat)

---

## 🔄 Évolutions futures

### Phase 2 (court terme)
- [ ] Envoi d'emails de confirmation
- [ ] API REST pour app mobile
- [ ] Système de notation des films
- [ ] Annulation de réservation (avec règles)

### Phase 3 (moyen terme)
- [ ] Paiement réel (Stripe/PayPal)
- [ ] Recommandations personnalisées (ML)
- [ ] Multi-cinémas (plusieurs établissements)
- [ ] Programme de fidélité

### Phase 4 (long terme)
- [ ] Intégration distributeurs automatiques
- [ ] Application mobile native (Android/iOS)
- [ ] Prévisions d'affluence (IA)
- [ ] Réalité augmentée (preview salles)

---

## 📚 Références

### Documentation technique
- [Jakarta Servlet Spec](https://jakarta.ee/specifications/servlet/5.0/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [iText 7](https://itextpdf.com/en/resources/api-documentation)

### Standards & Patterns
- [MVC Pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller)
- [DAO Pattern](https://www.oracle.com/java/technologies/dataaccessobject.html)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

---

**Document maintenu par l'équipe Baovola** - Dernière mise à jour : 8 janvier 2026
