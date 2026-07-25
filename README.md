
Readme · MD
# 📚 Bibliothèque API

API REST de gestion de bibliothèque développée avec **Spring Boot**, permettant la gestion des livres, auteurs, membres et emprunts, avec authentification sécurisée via JWT et gestion des rôles.

> Projet réalisé dans le cadre de ma montée en compétences en développement backend Java / Spring Boot.

## ✨ Fonctionnalités

- 📖 Gestion des livres et de leurs auteurs (relations JPA `@OneToMany` / `@ManyToOne`)
- 👤 Gestion des membres avec authentification complète (inscription, connexion, JWT)
- 🔄 Système d'emprunt avec règles métier (limite d'emprunts, disponibilité des exemplaires)
- 🔐 Authentification JWT + gestion des rôles (`ADMIN` / `MEMBRE`) appliquée par endpoint
- 🧱 DTOs dédiés en entrée et en sortie (aucune entité JPA jamais exposée directement, mot de passe jamais renvoyé)
- ✅ Validation des données (Bean Validation)
- ⚠️ Gestion centralisée des exceptions avec codes HTTP appropriés (`404`, `409`, `401`, `403`)
- 🔁 Protection contre les doublons (email, ISBN) et contre l'auto-attribution de rôle à l'inscription
## 🛠️ Stack technique

| Techno | Usage |
|---|---|
| Java | Langage principal |
| Spring Boot | Framework backend |
| Spring Data JPA / Hibernate | Persistance des données |
| Spring Security | Authentification et autorisation par rôle |
| JJWT (io.jsonwebtoken) | Génération et validation des tokens JWT |
| MySQL | Base de données |
| Lombok | Réduction du boilerplate |
| Maven | Gestion des dépendances |

## 🏗️ Architecture

Le projet suit une architecture en couches classique :

```
com.bibliotheque.bibliotheque_api
├── entity/       → Entités JPA (Livre, Auteur, Membre, Emprunt)
├── repository/   → Interfaces Spring Data JPA
├── service/      → Logique métier
├── controller/   → Endpoints REST
├── dto/
│   ├── request/  → DTOs entrants (LivreCreateRequest, RegisterRequest, LoginRequest...)
│   └── response/ → DTOs sortants (LivreResponse, MembreResponse, EmpruntResponse...)
├── mapper/       → Conversion Entity <-> DTO
├── exception/    → Exceptions custom + gestionnaire global (@RestControllerAdvice)
├── security/
│   ├── jwt/      → JwtService (génération/validation), JwtAuthFilter
│   └── config/   → SecurityConfig, CustomUserDetailsService
└── enums/        → Enumérations métier (Role, StatutEmprunt)
```

**Principes appliqués :**
- Les Controllers ne manipulent jamais d'entités JPA en entrée/sortie, uniquement des DTOs.
- Les Mappers restent `static` et sans dépendance (pas d'accès base) ; toute logique nécessitant un accès aux données reste dans les Services.
- Les Services centralisent toutes les règles métier et lèvent des exceptions custom typées plutôt que des `RuntimeException` génériques.
## 📋 Règles métier

- Un membre ne peut pas emprunter plus de **3 livres** simultanément (`EN_COURS`).
- Un livre ne peut être emprunté que s'il reste des **exemplaires disponibles** (comparé aux emprunts en cours sur ce livre).
- Un emprunt déjà `RENDU` ne peut pas être rendu une seconde fois.
- Un livre, un auteur ou un membre ne peuvent pas être supprimés s'ils sont encore liés à des données actives (emprunts en cours, livres associés).
- Un email ou un ISBN déjà utilisé est rejeté (`409 Conflict`) plutôt que de provoquer une erreur SQL brute.
- Deux rôles distincts : `ADMIN` (gestion des livres/auteurs) et `MEMBRE` (emprunts, consultation). Le rôle est toujours forcé à `MEMBRE` à l'inscription, impossible à modifier par le client.
## 🔐 Sécurité

- Mots de passe hashés avec **BCrypt**, jamais stockés ni renvoyés en clair.
- Authentification par **JWT** : un token est généré à la connexion (`/api/auth/login`) et doit être transmis dans l'en-tête `Authorization: Bearer <token>` pour accéder aux routes protégées.
- Un filtre (`JwtAuthFilter`) valide le token à chaque requête et recharge les permissions de l'utilisateur depuis la base en temps réel (un changement de rôle est pris en compte immédiatement, sans reconnexion).
- Autorisations par rôle appliquées au niveau de `SecurityConfig` (`hasRole`, `hasAnyRole`).
- Limite connue : pas de mécanisme de révocation de token (blacklist) — un token reste valide jusqu'à son expiration (10h) même si le compte est modifié entre-temps.
## 🚀 Installation et lancement

### Prérequis

- Java 21+
- Maven
- MySQL
### Configuration de la base de données

```sql
CREATE DATABASE bibliotheque_db;
```

### Variables d'environnement

```bash
export DB_PASSWORD=votre_mot_de_passe_mysql
```

### Lancer le projet

```bash
git clone https://github.com/nolanjean/bibliotheque-api.git
cd bibliotheque-api
mvn spring-boot:run
```

L'API est accessible sur `http://localhost:8080`.

## 📡 Endpoints principaux

### Authentification

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| POST | `/api/auth/register` | Inscription d'un membre (rôle forcé à `MEMBRE`) | Public |
| POST | `/api/auth/login` | Connexion, retourne un token JWT | Public |

### Livres

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/livres` | Liste des livres | Public |
| GET | `/api/livres/{id}` | Détail d'un livre | Public |
| POST | `/api/livres` | Créer un livre | `ADMIN` |
| PUT | `/api/livres/{id}` | Modifier un livre | `ADMIN` |
| DELETE | `/api/livres/{id}` | Supprimer un livre (bloqué si des exemplaires sont empruntés) | `ADMIN` |

### Auteurs

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/auteurs` | Liste des auteurs (avec titres de leurs livres) | Public |
| GET | `/api/auteurs/{id}` | Détail d'un auteur | Public |
| POST | `/api/auteurs` | Créer un auteur | `ADMIN` |
| PUT | `/api/auteurs/{id}` | Modifier un auteur | `ADMIN` |
| DELETE | `/api/auteurs/{id}` | Supprimer un auteur (bloqué s'il a encore des livres) | `ADMIN` |

### Membres

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/membres` | Liste des membres | Authentifié |
| GET | `/api/membres/{id}` | Détail d'un membre | Authentifié |
| PUT | `/api/membres/{id}` | Modifier un membre | Authentifié |
| DELETE | `/api/membres/{id}` | Supprimer un membre (bloqué s'il a des emprunts en cours) | Authentifié |

### Emprunts

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/emprunts` | Liste de tous les emprunts | `ADMIN`, `MEMBRE` |
| GET | `/api/emprunts/{id}` | Détail d'un emprunt | `ADMIN`, `MEMBRE` |
| GET | `/api/emprunts/membre/{membreId}` | Emprunts d'un membre donné | `ADMIN`, `MEMBRE` |
| POST | `/api/emprunts?membreId=&livreId=` | Emprunter un livre | `ADMIN`, `MEMBRE` |
| PUT | `/api/emprunts/{id}/rendre` | Rendre un livre | `ADMIN`, `MEMBRE` |

## 🧪 Tester l'API

Exemple de flux complet avec [Postman](https://www.postman.com/) ou `curl` :

```bash
# 1. Inscription
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nom":"Jean Dupont","email":"jean@email.com","motDePasse":"motdepasse123"}'
 
# 2. Connexion → récupère le token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jean@email.com","motDePasse":"motdepasse123"}'
 
# 3. Utiliser le token sur une route protégée
curl -X POST http://localhost:8080/api/emprunts?membreId=1&livreId=1 \
  -H "Authorization: Bearer <token>"
```

## 🎯 Objectif du projet

Ce projet a été développé pour consolider mes compétences en développement backend Java / Spring Boot : gestion des relations JPA, architecture en couches (Controller / Service / Repository / DTO / Mapper), sécurisation d'une API avec Spring Security et JWT, gestion centralisée des erreurs, et bonnes pratiques de développement assisté par IA (revue critique du code généré, compréhension systématique avant application).

## 👤 Auteur

**Nolan Jean**
- GitHub : [@nolanjean](https://github.com/nolanjean)