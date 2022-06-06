-- Droit à l'oubli
set autocommit off;

-- Suppression des données personnelles de l'utilisateur 2
DELETE FROM CLIENT
WHERE IDUTILISATEUR = 2;

-- Mise à jour de IDUTILISATEUR, on le modifie à 101
UPDATE  UTILISATEUR
SET IDUTILISATEUR = 101
WHERE IDUTILISATEUR = 2;

UPDATE  OFFRE
SET IDUTILISATEUR = 101
WHERE IDUTILISATEUR = 2

commit;