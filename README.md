# 🐾 PetClinic — Système de Gestion Vétérinaire

Application de gestion pour clinique vétérinaire, développée en équipe avec la stack JHipster / Spring Boot / Angular.

---

## Organisation du projet

| Groupe                      | Périmètre                             | Livrable                                 |
| --------------------------- | ------------------------------------- | ---------------------------------------- |
| G1 — Cliniques              | CRUD Clinique                         | Formulaires, validations, liste          |
| G2 — Médecins               | CRUD Médecin, rattaché à une clinique | Filtre par clinique/spécialité           |
| G3 — Clients                | CRUD Client                           | Recherche par nom, fiche client          |
| G4 — Animaux                | CRUD Animal, rattaché à un client     | Filtre par espèce, historique poids      |
| G5 — Rendez-vous            | Planification RDV                     | Règle "pas de RDV dans le passé"         |
| G6 — Recherche & Dashboard  | Page d'accueil                        | KPI, planning du jour, recherche globale |
| G7 — Intégration & JHipster | Pilotage technique                    | JDL, merges, CI, documentation           |

---

## Stack technique

| Paramètre        | Valeur                      |
| ---------------- | --------------------------- |
| Générateur       | JHipster 9.2.0              |
| Type             | Monolithique                |
| Backend          | Spring Boot, Java 21, Maven |
| Frontend         | Angular                     |
| Base de données  | PostgreSQL                  |
| Authentification | JWT (stateless)             |
| Cache            | Ehcache                     |
| i18n             | Activée (français)          |

---

## Prérequis

- Java 21+
- Node.js ≥ 24.18 et npm
- Docker (pour la base PostgreSQL)

---

## Lancer le projet

**1. Installer les dépendances npm :**

```bash
npm install
```

**2. Démarrer la base de données PostgreSQL :**

```bash
npm run docker:db:up
```

**3. Démarrer le backend (port 8080) :**

```bash
./mvnw
```

**4. Démarrer le frontend (port 4200) :**

```bash
npm start
```

L'application est accessible sur `http://localhost:4200`.

Identifiants par défaut : `admin` / `admin`

---

## Lancer les tests

**Tests backend :**

```bash
./mvnw test
```

**Tests frontend :**

```bash
npm test
```

---

## Modèle de données

Entités : `Clinique`, `Medecin`, `Client`, `Animal`, `RendezVous`, `PeserAnimal`

Le fichier `petclinic.jdl` à la racine du dépôt est la source unique de vérité pour le modèle. Toute modification passe par une Pull Request reviewée par au moins 2 groupes.

---

## Documentation technique

Voir [`DOCUMENTATION.md`](./DOCUMENTATION.md) pour le détail par module.
