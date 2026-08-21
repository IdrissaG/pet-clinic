# 🐾 PetClinic — Jour 5 : Finitions, documentation et préparation de la démo

L'application est fonctionnellement complète et globalement testée. Aujourd'hui : on finalise, on documente, et on prépare la présentation finale.

---

## 0. Avant de commencer (15 min, tous les groupes)

- [ ] `git pull` sur `main`, vérifier que l'application démarre proprement avec l'ensemble des modules.

---

## 1. Tâches par groupe

### G1 — Cliniques

- [ ] Derniers ajustements visuels sur la liste et le formulaire.
- [ ] Vérifier que les messages d'erreur (validation téléphone/adresse) sont clairs pour un utilisateur non technique.
- [ ] Rédiger une courte section dans la doc technique (voir section 3) sur le module Clinique.

### G2 — Médecins

- [ ] Derniers ajustements sur le filtre clinique/spécialité.
- [ ] Vérifier la cohérence de l'affichage médecin ↔ clinique sur toutes les pages où il apparaît (RDV, fiche clinique).
- [ ] Rédiger la section doc technique du module Médecin.

### G3 — Clients

- [ ] Derniers ajustements sur la fiche client (affichage des animaux rattachés).
- [ ] Vérifier le comportement de suppression d'un client ayant des animaux (bloquer ou avertir — décision à documenter).
- [ ] Rédiger la section doc technique du module Client.

### G4 — Animaux

- [ ] Derniers ajustements sur le filtre par espèce et l'affichage du carnet de santé simplifié.
- [ ] Vérifier la cohérence des données de démo (avoir un jeu d'animaux varié pour la présentation).
- [ ] Rédiger la section doc technique du module Animal.

### G5 — Rendez-vous

- [ ] Derniers ajustements sur la vue liste/calendrier des rendez-vous.
- [ ] Préparer un jeu de données de démo réaliste : quelques RDV aujourd'hui, quelques RDV à venir, pour que le dashboard et la vue "RDV du jour" aient du contenu à montrer.
- [ ] Rédiger la section doc technique du module Rendez-vous (règle métier incluse).

### G6 — Recherche & Dashboard

- [ ] Vérifier une dernière fois la cohérence visuelle des barres de recherche sur tous les modules.
- [ ] S'assurer que le dashboard affiche des chiffres cohérents avec les données de démo préparées par les autres groupes.
- [ ] Rédiger la section doc technique du module Recherche/Dashboard.

### G7 (rôle en place) — Intégration & JHipster

- [ ] Merger les dernières PR en attente, vérifier que `main` est stable et que la CI est verte.
- [ ] Compiler les sections de documentation technique rédigées par chaque groupe en un seul document cohérent (`DOCUMENTATION.md`).
- [ ] Vérifier que le `README.md` est à jour (installation, lancement, structure du dépôt, historique des groupes).
- [ ] Préparer un export/screenshot du modèle de données final (JDL Studio ou équivalent) pour la présentation.

---

## 2. Documentation technique attendue

Chaque groupe fournit une courte section (10-15 lignes) contenant :

- le périmètre du module,
- les choix techniques ou règles métier notables,
- les difficultés rencontrées et comment elles ont été résolues (utile notamment pour les conflits JDL/Git).

---

## ✅ En fin de journée, on doit avoir :

1. Une application stable sur `main`, sans bug bloquant connu.
2. Un jeu de données de démo cohérent entre tous les modules.
3. Une documentation technique complète (`DOCUMENTATION.md` + `README.md` à jour).
