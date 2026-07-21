# 📚 Bibliothèque API

API REST de gestion de bibliothèque développée avec **Spring Boot**, permettant la gestion des livres, auteurs, membres et emprunts, avec authentification sécurisée via JWT.

> Projet réalisé dans le cadre de ma montée en compétences en développement backend Java / Spring Boot.

## ✨ Fonctionnalités

- 📖 Gestion des livres et de leurs auteurs (relations JPA)
- 👤 Gestion des membres avec authentification (Spring Security + JWT)
- 🔄 Système d'emprunt avec règles métier (limite d'emprunts, disponibilité des exemplaires)
- 🔐 Gestion des rôles (`ADMIN` / `MEMBRE`)
- ✅ Validation des données (Bean Validation)
- ⚠️ Gestion centralisée des exceptions

## 🛠️ Stack technique

| Techno | Usage |
|---|---|
| Java | Langage principal |
| Spring Boot | Framework backend |
| Spring Data JPA / Hibernate | Persistance des données |
| Spring Security + JWT | Authentification et autorisation |
| MySQL | Base de données |
| Lombok | Réduction du boilerplate |
| Maven | Gestion des dépendances |

## 🏗️ Architecture

Le projet suit une architecture en couches classique :

```
com.bibliotheque.bibliotheque_api
├── entity/       → Entités JPA (Livre, Auteur, Membre, Emprunt)
├── repository/   → Interfaces Spring Data JPA
├── service/       → Logique métier
├── controller/   → Endpoints REST
├── dto/          → Objets de transfert (requêtes / réponses)
├── mapper/       → Conversion Entity <-> DTO
├── exception/    → Exceptions custom + gestion globale des erreurs
├── security/     → Configuration Spring Security & JWT
└── enums/        → Enumérations métier (Role, StatutEmprunt)
```

## 📋 Règles métier

- Un membre ne peut pas emprunter plus de **3 livres** simultanément.
- Un livre ne peut être emprunté que s'il reste des **exemplaires disponibles**.
- Deux rôles distincts : `ADMIN` (gestion des livres/auteurs) et `MEMBRE` (emprunts, consultation).

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

> À compléter au fur et à mesure du développement

| Méthode | Endpoint | Description | Rôle requis |
|---|---|---|---|
| POST | `/api/auth/register` | Inscription d'un membre | Public |
| POST | `/api/auth/login` | Connexion (retourne un JWT) | Public |
| GET | `/api/livres` | Liste des livres | Authentifié |
| POST | `/api/livres` | Ajouter un livre | ADMIN |
| POST | `/api/emprunts` | Emprunter un livre | MEMBRE |

## 🎯 Objectif du projet

Ce projet a été développé pour consolider mes compétences en développement backend Java / Spring Boot : gestion des relations JPA, sécurisation d'une API avec JWT, architecture en couches, et bonnes pratiques de développement (DTO, gestion d'exceptions, validation).

## 👤 Auteur

**Nolan Jean**
- GitHub : [@nolanjean](https://github.com/nolanjean)