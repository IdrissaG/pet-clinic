# Documentation technique — PetClinic

---

## Module Clinique (G1)

_Section à compléter par G1._

**Périmètre :**

**Choix techniques / règles métier notables :**

**Difficultés rencontrées et solutions :**

---

## Module Médecin (G2)

**Périmètre :**
Gestion du référentiel des médecins : CRUD complet, rattachement obligatoire à une clinique, filtrage de la liste par clinique et par spécialité (recherche partielle, insensible à la casse).

**Choix techniques / règles métier notables :**

- **Suppression protégée** : Un médecin ayant des rendez-vous rattachés ne peut pas être supprimé (`MedecinService.delete` vérifie `RendezVousRepository.existsByMedecinId` et lève `MedecinAvecRendezVousException` avec un code 400).
- **Filtrage backend** : Endpoint `GET /api/medecins` acceptant `cliniqueId` et `specialite` via une requête JPQL unique (`MedecinRepository.findByFilters`).
- **Harmonisation UI** : Rebranchement des filtres fonctionnels (`cliniqueFilter`, `specialiteFilter`, `resetFilters()`) sur la barre de recherche harmonisée par G6.

**Difficultés rencontrées et solutions :**

- Respect de l'architecture ArchUnit (isolation couche service/web) : `MedecinAvecRendezVousException` duplique volontairement `DEFAULT_TYPE` au lieu d'importer `ErrorConstants`.
- Conflits JDL lors des rebases : résolus en régénérant uniquement l'entité `Medecin`.

---

## Module Client (G3)

**Périmètre :**
Gestion complète des propriétaires d'animaux (CRUD, pagination, recherche et affichage des animaux rattachés).

**Choix techniques / règles métier notables :**

- **Suppression en cascade** : Gestion de la relation `@OneToMany` avec `Animal` via `CascadeType.ALL`, `orphanRemoval = true` et `@Transactional` sur l'endpoint DELETE.
- **Prévention frontend** : La modale `ClientDeleteDialog` effectue une vérification dynamique en temps réel et affiche le nombre d'animaux impactés.

**Difficultés rencontrées et solutions :**

- Blocage du rendu Angular : résolu par l'enregistrement des icônes FontAwesome manquantes dans `FaIconLibrary`.
- Erreurs de sérialisation JSON récursive et conflits d'imports TypeScript (`DecimalPipe`, `TranslatePipe`) survenus lors des rebases Git.

---

## Module Animal (G4)

**Périmètre :**
Gestion de la fiche animal (nom, espèce, date de naissance, sexe) et suivi du poids via l'entité `PeserAnimal` (liée à un rendez-vous). La fiche détail affiche le dernier poids et l'historique complet trié par date décroissante.

**Choix techniques / règles métier notables :**

- Enums pour `Espece` (11 valeurs) et `Sexe`. Validations `@PastOrPresent` (`dateNaissance`) et `@Positive`/`@DecimalMin` (`poids`).
- Filtre par espèce avec pagination backend (Criteria / QueryService).
- Architecture réactive Angular : utilisation du pattern `signals`/`httpResource` pour dériver l'historique de poids sans rechargement récursif.

**Difficultés rencontrées et solutions :**

- Mot-clé `filter` manquant dans le JDL commun : signalé à G7 et corrigé après réimport.
- Dépendance inter-modules (G4/G5) : sélection `RendezVous` vide dans `PeserAnimal` par manque de jeux de données de test côté G5.
- Correction de bugs d'affichage (IDs affichés à la place des noms, balises HTML manquantes) et nettoyage des conflits d'imports lors des merges.

---

## Module Rendez-vous (G5)

**Périmètre :**
CRUD complet des rendez-vous (interconnecté à `Animal`, `Medecin` et `Clinique`), liste avec recherche/tri, regroupement par jour et endpoint dédié `GET /api/rendez-vous/today` pour le dashboard.

**Choix techniques / règles métier notables :**

- Format de date en `Instant`.
- Interdiction des rendez-vous dans le passé et vérification de la cohérence médecin/clinique en base de données.
- Regroupement réactif par jour via `computed()` Angular.

**Difficultés rencontrées et solutions :**

- Bug transverse dans `alert-error.ts` (signal non mis à jour) bloquant l'affichage des erreurs applicatives.
- Contrôle médecin/clinique erroné introduit par un autre groupe : corrigé pour vérifier l'état en base plutôt que le payload HTTP.
- Instabilités de l'environnement local après reclonage (Docker, ports, version du JDK).

---

## Module Recherche & Dashboard (G6)

**Périmètre :**
Gestion de la vue d'accueil (`jhi-home`), du tableau de bord décisionnel (KPIs : total animaux, rendez-vous du jour) relié à G2 et G5, de la recherche globale multi-critères et de l'i18n.

**Choix techniques / règles métier notables :**

- Architecture Angular 100% Signals (`signal`, `computed`) pour un filtrage réactif côté client sans rechargement.
- Refonte de l'interface d'accueil natif JHipster pour une UI responsive avec couverture i18n intégrale via `jhiTranslate`.

**Difficultés rencontrées et solutions :**

- Parsing défensif dans les `computed()` pour gérer la divergence des formats de dates (DayJS vs ISO Strings).
- Utilisation temporaire de données de test (mocks) pendant la stabilisation du fichier JDL partagé avant le câblage direct des API REST.

---

## Module Intégration & JHipster (G7)

**Périmètre :**
Pilotage technique du dépôt commun — gestion du JDL, suivi des merges, intégration continue (CI) et documentation globale.

**Choix techniques / règles métier notables :**

- Initialisation et configuration de la structure du projet sous JHipster 9.2.0.
- Intégration d'un pipeline CI GitHub Actions (build + exécution des tests backend).
- Modèle de données centralisé via un fichier unique `petclinic.jdl`, toute modification étant soumise à une Pull Request.

**Difficultés rencontrées et solutions :**

- Violation ArchUnit sur `RendezVousDatePasseeException` (placée dans un mauvais package) → déplacée vers `web.rest.errors`.
- Detection et signalement de fichiers de test mal positionnés dans `src/main/java` au lieu de `src/test/java`.
- Alertes GitGuardian répétées sur le secret JWT par défaut de JHipster (PR #2, #9, #10) → identifiées et documentées dans `README-G7.md`.
- Coordination du JDL partagé entre 6 groupes → mise en place d'une procédure stricte (PR obligatoire, 2 validations minimum, documentée dans `README-JDL.md`).
