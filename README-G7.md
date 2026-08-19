# README-G7: Documentation technique du projet

Ce document résume la configuration technique du projet, les prérequis, les commandes pour le faire tourner, et comment lancer les tests. Il complète le [README.md](./README.md) principal.

---

## 1. Informations sur le projet

Le projet a été généré avec **JHipster 9.2.0**. Voici les choix retenus lors de la génération :

| Paramètre                       | Valeur                                                            |
| ------------------------------- | ----------------------------------------------------------------- |
| Nom de l'application            | `petclinic`                                                       |
| Type d'application              | Monolithique                                                      |
| Package Java                    | `com.stg.petclinic`                                               |
| Build tool                      | Maven                                                             |
| Reactive (WebFlux)              | Non                                                               |
| Authentification                | JWT (stateless, par token)                                        |
| Type de base de données         | SQL                                                               |
| Base de données (production)    | PostgreSQL                                                        |
| Base de données (développement) | PostgreSQL (nécessite Docker ou une base configurée manuellement) |
| Cache                           | Ehcache (cache local, single node)                                |
| Cache 2nd niveau Hibernate      | Activé                                                            |
| Framework client                | Angular                                                           |
| Admin UI                        | Générée                                                           |
| Thème Bootswatch                | Défaut JHipster                                                   |
| Internationalisation (i18n)     | Activée                                                           |
| Langue native                   | Français                                                          |

### Entités du modèle métier

`Clinique`, `Medecin`, `Client`, `Animal`, `RendezVous`, `PeserAnimal`

---

## 2. Prérequis

- **Java 21+**
- **Node.js ≥ 24.18** et npm
- **Docker** (nécessaire pour la base PostgreSQL en développement)
- Le wrapper Maven `./mvnw` (déjà fourni dans le dépôt, pas besoin d'installer Maven à part)

Installation des dépendances, une fois le dépôt cloné :

```bash
npm install
```

---

## 3. Cas particulier : PostgreSQL + Docker

Le projet utilise **PostgreSQL** aussi bien en développement qu'en production. En développement, la base tourne dans un conteneur Docker défini dans `src/main/docker/postgresql.yml`.

### Démarrer la base PostgreSQL

```bash
npm run docker:db:up
```

Cette commande lance le conteneur PostgreSQL et attend qu'il soit prêt (`docker compose ... up --wait`). Elle équivaut à la commande Docker suivante, qui pointe vers le fichier de configuration du conteneur :

```bash
docker compose -f src/main/docker/postgresql.yml up --wait
```

Connexion utilisée par défaut (profil `dev`) :

| Paramètre    | Valeur                                       |
| ------------ | -------------------------------------------- |
| URL          | `jdbc:postgresql://localhost:5432/petclinic` |
| Utilisateur  | `petclinic`                                  |
| Mot de passe | `petclinic`                                  |

### Arrêter / réinitialiser la base

```bash
npm run docker:db:down
```

> Cette commande supprime aussi le volume (`down -v`) : toutes les données locales sont perdues.

---

## 4. Lancer le projet en développement

1. Démarrer PostgreSQL via Docker :

   ```bash
   npm run docker:db:up
   ```

2. Démarrer le backend (Spring Boot, port `8080`) :

   ```bash
   ./mvnw
   ```

   Une fois démarré, le backend est accessible sur `http://localhost:8080`.

3. Dans un second terminal, démarrer le frontend (Angular, avec hot-reload) :

   ```bash
   npm start
   ```

   L'application est ensuite accessible sur `http://localhost:4200` (le frontend proxie les appels API vers le backend sur le port `8080`).

4. Connectez-vous avec le compte administrateur par défaut :

   | Utilisateur | Mot de passe |
   | ----------- | ------------ |
   | `admin`     | `admin`      |

   Une fois connecté en tant qu'`admin`, dans le menu en haut de la page apparaît l'option pour voir les **Entities** : il donne accès aux listes et formulaires CRUD de toutes les entités (`Clinique`, `Medecin`, `Client`, `Animal`, `RendezVous`, `PeserAnimal`).

---

# Fichiers à ne pas modifier : secrets d'exemple JHipster

Les fichiers suivants contiennent un secret JWT d'exemple **public**, généré par
défaut par JHipster (documenté sur jhipster.tech) :

- `src/main/resources/config/application-secret-samples.yml`
- `src/main/docker/jhipster-control-center.yml`

Ce n'est pas un identifiant réel compromis, voir PR #2, #9, #10 pour le contexte
complet. GitGuardian les signale comme "secret détecté", mais toute PR qui ne
touche pas ces fichiers n'est pas concernée par cette alerte.

**Ne modifiez pas ces fichiers** sans nécessité, pour éviter de redéclencher
inutilement un scan GitGuardian dessus.
