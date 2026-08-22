## Module Clinique (G1)

**Perimetre** : Gestion CRUD des cliniques veterinaires (nom, adresse, telephone), avec relations vers les medecins et rendez-vous qui y sont rattaches.

**Choix techniques et regles metier** :

- Validation du telephone au format senegalais strict (+221 suivi de 9 chiffres), appliquee cote backend (annotation @Pattern sur l'entite) et cote frontend (validators Angular), avec message d'erreur explicite affiche a l'utilisateur en cas de format invalide.
- L'adresse est un champ obligatoire non vide.
- Regle metier : suppression bloquee si des medecins sont rattaches a la clinique (verification via existsByCliniqueId dans le repository, exception dediee avec message clair cote interface).
- Personnalisation visuelle : tri fonctionnel sur toutes les colonnes de la liste (ID, nom, adresse, telephone), libelles en francais, code couleur coherent sur les boutons d'action (Voir/Editer/Supprimer/Retour/Annuler/Sauvegarder).

**Difficultes rencontrees** :

- Conflits Git recurrents sur les fichiers de traduction (clinique.json) et sur l'entite Clinique.java lors des fusions avec main, notamment lies a l'echappement des regex Java (\+ vs \\+) qui cassait la compilation apres chaque fusion, resolu en verifiant systematiquement la compilation apres chaque merge.
- Le hook de formatage automatique (Husky/Spotless) reecrivait parfois les regex, necessitant une verification post-commit.
