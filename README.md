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
- ⚠️ Gestion centralisée des exceptions avec un **format d'erreur JSON unique** et des codes HTTP appropriés (`400`, `401`, `403`, `404`, `409`), y compris pour les erreurs levées par Spring Security
- 🔁 Protection contre les doublons (email, ISBN) et contre l'auto-attribution de rôle à l'inscription
- 📄 Pagination (`Pageable`/`Page<T>`) sur **toutes** les listes (livres, auteurs, membres, emprunts), avec une structure JSON stable (`PagedModel`) et une taille de page limitée à 100
- 🔎 Recherche, filtres et tri sur les livres, auteurs et membres, avec une liste blanche des champs triables
- 📝 Logs applicatifs SLF4J (INFO sur les actions métier réussies, WARN sur les cas anormaux prévus, ERROR avec stack trace sur les erreurs non prévues)
- 📚 Documentation interactive Swagger/OpenAPI détaillée (descriptions, exemples, codes de réponse) avec support de l'authentification JWT
- ⚙️ Profils Spring `dev`/`prod` séparés (base locale vs variables d'environnement, niveaux de logs, Swagger désactivé en prod)
- 🧪 Suite de tests unitaires (JUnit 5 + Mockito) couvrant l'ensemble des Services

## 🛠️ Stack technique

| Techno | Usage |
|---|---|
| Java | Langage principal |
| Spring Boot | Framework backend |
| Spring Data JPA / Hibernate | Persistance des données (dont Specifications pour la recherche) |
| Spring Security | Authentification et autorisation par rôle |
| JJWT (io.jsonwebtoken) | Génération et validation des tokens JWT |
| MySQL | Base de données |
| Lombok | Réduction du boilerplate |
| Maven | Gestion des dépendances |
| springdoc-openapi | Documentation interactive Swagger/OpenAPI |
| SLF4J | Logs applicatifs |
| JUnit 5 / Mockito | Tests unitaires |

## 🏗️ Architecture

Le projet suit une architecture en couches classique :

```
com.bibliotheque.bibliotheque_api
├── entity/         → Entités JPA (Livre, Auteur, Membre, Emprunt)
├── repository/     → Interfaces Spring Data JPA
├── specification/  → Critères de recherche dynamiques (LivreSpecs, AuteurSpecs, MembreSpecs)
├── service/        → Logique métier
├── controller/     → Endpoints REST
├── dto/
│   ├── request/    → DTOs entrants (LivreCreateRequest, MembreUpdateRequest, RegisterRequest, LoginRequest...)
│   └── response/   → DTOs sortants (LivreResponse, MembreResponse, EmpruntResponse, LoginResponse...)
├── mapper/         → Conversion Entity <-> DTO
├── util/           → Utilitaires (TriUtils : liste blanche des champs de tri)
├── exception/      → Exceptions custom, ApiError (format d'erreur) et gestionnaire global (@RestControllerAdvice)
├── security/
│   ├── jwt/        → JwtService (génération/validation), JwtAuthFilter
│   └── config/     → SecurityConfig, CustomUserDetailsService, RestAuthenticationEntryPoint, RestAccessDeniedHandler
└── enums/          → Enumérations métier (Role, StatutEmprunt)
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
- La suppression d'un membre ou d'un livre sans emprunt en cours supprime aussi son historique d'emprunts (transaction atomique `@Transactional` : tout est annulé en cas d'erreur).
- Un `MEMBRE` ne voit que ses propres emprunts et ne peut emprunter que pour lui-même ; seul l'`ADMIN` peut emprunter au nom d'un membre et déclarer qu'un livre a été rendu.
- Un email ou un ISBN déjà utilisé est rejeté (`409 Conflict`) plutôt que de provoquer une erreur SQL brute, y compris lors de la modification d'un email.
- La modification d'un membre est partielle et limitée au nom, à l'email et au mot de passe (hashé) : le rôle ne peut jamais être modifié par ce biais.
- Deux rôles distincts : `ADMIN` (gestion complète : livres, auteurs, membres, emprunts, déclaration des retours) et `MEMBRE` (consultation du catalogue, ses propres emprunts, emprunter pour lui-même). Le rôle est toujours forcé à `MEMBRE` à l'inscription, impossible à modifier par le client.

## 🔐 Sécurité

- Mots de passe hashés avec **BCrypt**, jamais stockés ni renvoyés en clair.
- Authentification par **JWT** : un token est généré à la connexion (`/api/auth/login`) et doit être transmis dans l'en-tête `Authorization: Bearer <token>` pour accéder aux routes protégées.
- Un filtre (`JwtAuthFilter`) valide le token à chaque requête et recharge les permissions de l'utilisateur depuis la base en temps réel (un changement de rôle est pris en compte immédiatement, sans reconnexion). Un token invalide est ignoré : sur une route protégée, la requête est rejetée en `401`.
- Le token ne contient que l'email (`sub`), la date de création et l'expiration (10h) : le rôle n'y figure pas, il est rechargé depuis la base à chaque requête. Le front le récupère via la réponse du login ou `GET /api/auth/me`.
- Autorisations par rôle appliquées au niveau de `SecurityConfig` (`hasRole`, `hasAnyRole`).
- Les réponses `401` (non authentifié) et `403` (droits insuffisants) levées par Spring Security avant les controllers sont renvoyées dans le même format JSON que les autres erreurs.
- Contrôle d'accès sur les données : le membre est identifié par son token (`@AuthenticationPrincipal`) et jamais par un paramètre client, ce qui empêche un `MEMBRE` d'agir au nom d'un autre (`403 Forbidden` via un handler `AccessDeniedException` dédié).
- Le tri est limité à une liste blanche de champs par ressource : un tri sur un champ inconnu ou sensible (par exemple le mot de passe) est ignoré.
- La taille des pages est plafonnée à 100 éléments pour éviter de charger une table entière en une requête.
- Limites connues : pas de mécanisme de révocation de token (blacklist) — un token reste valide jusqu'à son expiration (10h) même si le compte est modifié entre-temps ; pas de limitation du nombre de tentatives de connexion.

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
| POST | `/api/auth/login` | Connexion, retourne le token JWT et le rôle (`{"token", "role"}`) | Public |
| GET | `/api/auth/me` | Membre connecté (id, nom, email, rôle), utile au front pour retrouver le rôle après un rechargement | Authentifié |

### Livres

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/livres?q=&auteurId=&page=0&size=10&sort=titre,asc` | Liste des livres (paginée, recherche, filtre par auteur, tri) | Public |
| GET | `/api/livres/{id}` | Détail d'un livre | Public |
| POST | `/api/livres` | Créer un livre | `ADMIN` |
| PUT | `/api/livres/{id}` | Modifier un livre (mise à jour partielle) | `ADMIN` |
| DELETE | `/api/livres/{id}` | Supprimer un livre (bloqué si des exemplaires sont empruntés) | `ADMIN` |

### Auteurs

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/auteurs?q=&page=0&size=10&sort=nom,asc` | Liste des auteurs, paginée, avec recherche par nom (avec titres de leurs livres) | Public |
| GET | `/api/auteurs/{id}` | Détail d'un auteur | Public |
| POST | `/api/auteurs` | Créer un auteur | `ADMIN` |
| PUT | `/api/auteurs/{id}` | Modifier un auteur (mise à jour partielle) | `ADMIN` |
| DELETE | `/api/auteurs/{id}` | Supprimer un auteur (bloqué s'il a encore des livres) | `ADMIN` |

### Membres

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/membres?q=&page=0&size=10&sort=nom,asc` | Liste des membres, paginée, avec recherche par nom ou email | `ADMIN` |
| GET | `/api/membres/{id}` | Détail d'un membre | `ADMIN` |
| PUT | `/api/membres/{id}` | Modifier le nom, l'email ou le mot de passe d'un membre (le rôle n'est pas modifiable) | `ADMIN` |
| DELETE | `/api/membres/{id}` | Supprimer un membre (bloqué s'il a des emprunts en cours, son historique est supprimé avec lui) | `ADMIN` |

### Emprunts

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| GET | `/api/emprunts/mes-emprunts?page=0&size=10` | Emprunts du membre connecté, paginés | `ADMIN`, `MEMBRE` |
| GET | `/api/emprunts?page=0&size=10` | Liste de tous les emprunts, paginée | `ADMIN` |
| GET | `/api/emprunts/{id}` | Détail d'un emprunt | `ADMIN` |
| GET | `/api/emprunts/membre/{membreId}?page=0&size=10` | Emprunts d'un membre donné, paginés | `ADMIN` |
| POST | `/api/emprunts?livreId=` | Emprunter un livre pour soi-même (le membre est lu depuis le token) | `ADMIN`, `MEMBRE` |
| POST | `/api/emprunts?livreId=&membreId=` | Emprunter un livre au nom d'un membre (`membreId` refusé en `403` pour un `MEMBRE`) | `ADMIN` |
| PUT | `/api/emprunts/{id}/rendre` | Déclarer le retour d'un livre | `ADMIN` |

### Recherche, filtres et tri

| Ressource | Recherche (`q`) | Filtre | Champs triables (`sort`) |
|---|---|---|---|
| Livres | titre ou ISBN | `auteurId` | `id`, `titre`, `isbn` |
| Auteurs | nom | — | `id`, `nom` |
| Membres | nom ou email | — | `id`, `nom`, `email` |
| Emprunts | — | — | `id`, `dateEmprunt`, `dateRetourPrevue`, `statut` |

- La recherche est insensible à la casse et porte sur une partie du texte.
- Le tri se fait avec `sort=champ,asc` ou `sort=champ,desc`. Un champ qui n'est pas dans la liste est ignoré (la liste est alors renvoyée dans l'ordre par défaut).
- Les paramètres peuvent se combiner : `GET /api/livres?q=misérables&auteurId=1&sort=titre,asc&size=5`.

### Format des listes paginées

Les endpoints paginés renvoient les éléments dans `content` et les informations de pagination dans l'objet `page` :

```json
{
  "content": [ { "id": 1, "titre": "..." } ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 42,
    "totalPages": 5
  }
}
```

La taille de page maximale est de 100 : une valeur supérieure est ramenée à 100.

La structure est stabilisée via `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)`, plutôt que de sérialiser directement les `PageImpl` (format non garanti entre les versions de Spring Data).

### Format des erreurs

Toutes les erreurs de l'API ont la même forme :

```json
{
  "status": 404,
  "erreur": "Livre introuvable avec l'ID 42"
}
```

Pour une erreur de validation (`400`), le détail par champ est fourni dans `champs` :

```json
{
  "status": 400,
  "erreur": "Données invalides",
  "champs": {
    "email": "Email invalide",
    "motDePasse": "Le mot de passe doit contenir au moins 8 caractères"
  }
}
```

| Code | Situation |
|---|---|
| `400` | Données invalides, JSON mal formé, paramètre de mauvais type ou manquant |
| `401` | Identifiants incorrects, token absent ou invalide |
| `403` | Droits insuffisants (rôle ou action sur les données d'un autre membre) |
| `404` | Ressource ou route introuvable |
| `405` | Méthode HTTP non supportée sur cette route |
| `409` | Conflit métier (doublon, limite d'emprunts, livre indisponible, suppression bloquée...) |
| `500` | Erreur interne imprévue (message générique, détail uniquement dans les logs) |

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

# 3. Rechercher un livre
curl "http://localhost:8080/api/livres?q=misérables&sort=titre,asc"

# 4. Utiliser le token sur une route protégée
curl -X POST "http://localhost:8080/api/emprunts?livreId=1" \
  -H "Authorization: Bearer <token>"
```

## 📚 Documentation interactive (Swagger)

L'API expose une documentation interactive générée avec **springdoc-openapi**, accessible sur :

```
http://localhost:8080/swagger-ui.html
```

Chaque endpoint est décrit (règles métier, paramètres, codes de réponse avec exemples d'erreur). Le bouton **Authorize** permet de coller un token JWT (récupéré via `/api/auth/login`) et de tester directement les routes protégées depuis l'interface. Swagger est désactivé automatiquement en profil `prod`.

## ⚙️ Profils Spring

Le projet distingue deux profils, activés via `spring.profiles.active` :

| Profil | Usage | Particularités |
|---|---|---|
| `dev` (par défaut) | Développement local | Base MySQL locale, `ddl-auto=update`, logs `DEBUG`, Swagger activé |
| `prod` | Production | Datasource via variables d'environnement (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), `ddl-auto=validate` (pas de modification automatique du schéma), logs `WARN`, Swagger désactivé |

## 🧪 Tests

Le projet dispose d'une suite de **tests unitaires** (JUnit 5 + Mockito) sur la couche Service, isolée de toute base de données via mocks des Repositories.

Chaque Service (`LivreService`, `AuteurService`, `MembreService`, `EmpruntService`) est couvert sur :
- les cas de succès de chaque méthode métier (création, mise à jour, suppression, emprunt, retour, recherche)
- les cas d'échec métier prévus (ISBN/email déjà existant, limite d'emprunts atteinte, livre indisponible, emprunt déjà rendu, ressource encore liée à des données actives)
- les cas de ressource introuvable (`RessourceNotFoundException`)
- la sécurisation du tri (un champ non autorisé, comme le mot de passe, est ignoré)

Lancer les tests :

```bash
mvn test
```

## 🎯 Objectif du projet

Ce projet a été développé pour consolider mes compétences en développement backend Java / Spring Boot : gestion des relations JPA, architecture en couches (Controller / Service / Repository / DTO / Mapper), sécurisation d'une API avec Spring Security et JWT, gestion centralisée des erreurs, et bonnes pratiques de développement assisté par IA (revue critique du code généré, compréhension systématique avant application).

## 👤 Auteur

**Nolan Jean**
- GitHub : [@nolanjean](https://github.com/nolanjean)