# 🐾 PetClinic — Jour 3 : Recherche, intégration et premières fusions

Les modules de base commencent à prendre forme. Aujourd'hui : on branche la recherche/filtrage, on avance sur les règles métier, et on fait les **premières vraies fusions vers `main`**.

---

## 0. Avant de commencer (15 min, tous les groupes)

- [ ] `git pull` sur `main`.
- [ ] Vérifier si des changements sur `petclinic.jdl` ont été mergés depuis hier (voir demandes remontées par G7) et les récupérer sur sa branche (`git rebase main` ou `git merge main`).
- [ ] Vérifier que l'application démarre toujours après récupération des derniers changements.

---

## 1. Tâches par groupe

### G1 — Cliniques

- [ ] Finaliser la personnalisation de la liste (tri, libellés).
- [ ] Implémenter la recherche par nom de clinique sur la liste.
- [ ] Tester le cas limite : suppression d'une clinique qui a des médecins rattachés (voir avec G2 quel comportement adopter — blocage ou confirmation).

### G2 — Médecins

- [ ] Implémenter le filtre par clinique et par spécialité.
- [ ] Finaliser le formulaire (sélection clinique lisible, validation email).
- [ ] Se coordonner avec G1 sur le comportement en cas de suppression de clinique.

### G3 — Clients

- [ ] Implémenter la recherche par nom/prénom.
- [ ] Sur la fiche client, afficher la liste des animaux rattachés (coordination avec G4 sur le format d'affichage).
- [ ] Finaliser les validations de formulaire.

### G4 — Animaux

- [ ] Implémenter le filtre par espèce.
- [ ] Finaliser l'affichage du poids et de la date de naissance (formatage lisible).
- [ ] Fournir à G3 le format d'affichage résumé d'un animal (nom + espèce) pour la fiche client.

### G5 — Rendez-vous

- [ ] Finaliser la règle métier "pas de rendez-vous dans le passé" côté back-end, avec message d'erreur clair renvoyé au front.
- [ ] Afficher ce message d'erreur proprement côté Angular (pas juste une erreur 400 brute).
- [ ] Implémenter le filtre "rendez-vous du jour" sur la liste (utile aussi pour G6).

### G6 — Recherche & Dashboard

- [ ] Câbler le dashboard sur les vraies données : nombre total d'animaux (appel à l'API `Animal`), rendez-vous du jour (réutiliser le filtre de G5).
- [ ] Avancer sur l'harmonisation visuelle des barres de recherche entre les modules (cohérence avec G1/G2/G3/G4).
- [ ] Vérifier les traductions i18n restantes sur les libellés génériques.

### G7 — Intégration & JHipster

- [ ] Traiter en une seule PR groupée les demandes de modification du JDL remontées hier (nouveaux attributs, ajustements de relations).
- [ ] Réimporter le JDL mis à jour et vérifier que ça ne casse rien pour les groupes déjà avancés.
- [ ] Faire les premières revues de code sur les PR ouvertes hier (G1 à G6) et guider vers un premier merge propre dans `main`.
- [ ] Vérifier que la CI passe bien sur ces PR avant merge.

---

## 2. Premières fusions dans `main` (tous les groupes, avec G7)

- [ ] Chaque binôme finalise sa PR de la veille (retire le "Draft/WIP" si le module est stable).
- [ ] Revue croisée : chaque PR est relue par un membre **d'un autre groupe**, pas seulement par G7.
- [ ] Résoudre les conflits éventuels sur les fichiers générés (changelogs Liquibase, fichiers i18n, `module.ts` Angular) — c'est normal et attendu, ne pas paniquer.
- [ ] Merge dans `main` dès que la review est validée et la CI verte.

---

## 3. Point d'intégration de fin de journée (15-20 min)

- [ ] Démo rapide de chaque module (2 min max par groupe) : ce qui marche, ce qui reste à faire.
- [ ] G7 fait un point sur l'état du JDL et signale si une nouvelle vague de modifications est prévue pour demain.
- [ ] Identifier ensemble les dépendances entre groupes qui bloquent (ex. G6 a besoin que G5 ait fini son filtre "RDV du jour").

---

## ✅ En fin de journée, on doit avoir :

1. La recherche/filtrage fonctionnelle sur chaque liste (G1 à G4).
2. La règle métier des rendez-vous finalisée et proprement gérée côté front (G5).
3. Un dashboard connecté à des données réelles (G6).
4. Au moins une PR par groupe **mergée** dans `main` (pas juste ouverte).
5. Un JDL à jour intégrant les ajustements remontés depuis le jour 2.
