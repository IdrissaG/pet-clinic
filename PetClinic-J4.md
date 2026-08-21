# 🐾 PetClinic — Jour 4 : Consolidation, tests et rotation du rôle Intégration

La plupart des modules sont mergés dans `main`. Aujourd'hui : on consolide, on teste, et on fait tourner le rôle "Intégration" vers un nouveau groupe.

---

## 0. Avant de commencer (15 min, tous les groupes)

- [ ] `git pull` sur `main`, vérifier que l'application démarre avec l'ensemble des modules mergés.
- [ ] Faire un tour rapide de l'application dans son ensemble (pas seulement son propre module) : chaque binôme teste manuellement au moins un module d'un autre groupe et note les bugs/incohérences rencontrés.

---

## 1. Tâches par groupe

### G1 — Cliniques

- [ ] Écrire quelques tests (back-end : service/repository ; front : composant liste) sur le CRUD Clinique.
- [ ] Corriger les bugs remontés par les autres groupes lors du tour de test.
- [ ] Vérifier la cohérence avec G2 sur le comportement de suppression d'une clinique liée à des médecins.

### G2 — Médecins

- [ ] Écrire des tests sur la validation email et le filtre par clinique/spécialité.
- [ ] Corriger les bugs remontés.
- [ ] Finaliser l'affichage de la clinique de rattachement sur la fiche médecin.

### G3 — Clients

- [ ] Écrire des tests sur le CRUD Client et la recherche par nom.
- [ ] Vérifier avec G4 que l'affichage des animaux sur la fiche client est bien à jour et cohérent visuellement.
- [ ] Corriger les bugs remontés.

### G4 — Animaux

- [ ] Écrire des tests sur la validation du poids et de la date de naissance.
- [ ] Vérifier le filtre par espèce avec des données variées (ajouter quelques animaux de test de types différents).
- [ ] Corriger les bugs remontés.

### G5 — Rendez-vous

- [ ] Écrire des tests sur la règle métier "pas de rendez-vous dans le passé" (cas limite : rendez-vous à l'instant présent, fuseau horaire).
- [ ] Vérifier la cohérence des liens Animal/Médecin/Clinique sur un rendez-vous (ex. un médecin d'une autre clinique que celle du RDV — à bloquer ou non, à trancher en groupe).
- [ ] Corriger les bugs remontés.

### G6 — Recherche & Dashboard

- [ ] Vérifier que le dashboard reste correct après les corrections des autres groupes (nombre d'animaux, RDV du jour).
- [ ] Finaliser l'harmonisation visuelle des barres de recherche sur tous les modules.
- [ ] Faire un audit i18n complet : plus aucun libellé generé par défaut par JHipster ne doit rester non traduit.

### Nouveau G7 — Intégration & JHipster

- [ ] Reprendre la main sur le JDL et la CI (voir passage de relais ci-dessus).
- [ ] Centraliser les bugs remontés par le tour de test du matin dans des tickets/issues, un par groupe concerné.
- [ ] Vérifier que toutes les branches sont bien rebasées sur `main` à jour.
- [ ] Mettre en place (si pas déjà fait) un job CI qui lance aussi les tests écrits aujourd'hui par chaque groupe.

## 2. Lot de finitions transverses (à répartir selon les besoins du jour)

- [ ] Uniformiser les messages d'erreur affichés à l'utilisateur (format, ton) sur tous les formulaires.
- [ ] Vérifier la pagination sur toutes les listes (Clinique, Médecin, Client, Animal, RendezVous).
- [ ] Vérifier les responsive/affichages sur petit écran si le temps le permet.

---

## 3. Point d'intégration de fin de journée (15-20 min)

- [ ] Chaque groupe partage : bugs corrigés, bugs restants, tests écrits.
- [ ] Le nouveau G7 partage l'état global du dépôt (branches en retard, PR en attente, couverture de tests).
- [ ] Décider ensemble ce qui reste à faire avant la démo finale du projet.

---

## ✅ En fin de journée, on doit avoir :

1. Chaque module couvert par au moins quelques tests automatisés.
2. La majorité des bugs remontés lors du tour de test croisé corrigés.
3. Un rôle Intégration transmis proprement à un nouveau binôme.
4. Une application globalement cohérente visuellement (messages d'erreur, i18n, pagination).
