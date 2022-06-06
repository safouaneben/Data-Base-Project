
CREATE TABLE Categorie
(
  NomCategorie varchar(50) NOT NULL,
  CategorieMere varchar(50) NOT NULL,
  PRIMARY KEY (NomCategorie)
);


CREATE TABLE Produit
(
  IdProduit integer NOT NULL,
  Intitule varchar(50) NOT NULL,
  Descript varchar(255) NOT NULL,
  UrlImage varchar(50) NOT NULL,
  PrixCourant float NOT NULL,
  NomCategorie varchar(50) NOT NULL,
  PRIMARY KEY (IdProduit),
  FOREIGN KEY (NomCategorie) REFERENCES Categorie (NomCategorie)
);


CREATE TABLE Caracteristique
(
  NomCaracteristique varchar(50) NOT NULL,
  ValeurCaracteristique varchar(50) NOT NULL,
  IdProduit integer NOT NULL,
  PRIMARY KEY (NomCaracteristique, IdProduit),
  FOREIGN KEY (IdProduit) REFERENCES Produit (IdProduit)
);

CREATE TABLE Utilisateur
(
  IdUtilisateur integer NOT NULL,
  PRIMARY KEY (IdUtilisateur)
);


CREATE TABLE Client
(
  Nom varchar(50) NOT NULL,
  Prenom varchar(50) NOT NULL,
  MdpUtilisateur varchar(50) NOT NULL,
  AdresseMail varchar(100) NOT NULL,
  AdressePostale varchar(255) NOT NULL,
  IdUtilisateur integer NOT NULL,
  PRIMARY KEY (AdresseMail),
);


CREATE TABLE Offre
(
  DateHeure timestamp NOT NULL,
  IdProduit integer NOT NULL,
  PrixPropose float NOT NULL,
  IdUtilisateur integer NOT NULL,
  AchatReussi integer NOT NULL,
  PRIMARY KEY (DateHeure),
  FOREIGN KEY (IdProduit) REFERENCES Produit (IdProduit),
);
