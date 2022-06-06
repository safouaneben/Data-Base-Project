import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class Javasql {

    static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:Oracle1";
    static final String USER = "gangandm";
    static final String PASSWD = "gangandm";

    // Nombre d'offre pour acheter le produit
    static final int NB_OFFRE_TO_BUY = 5;

    // parcours des catégories recommandées générales
    static final String PRE_STMT_1 = "SELECT ADRESSEMAIL, MDPUTILISATEUR, IDUTILISATEUR FROM Client Where ADRESSEMAIL = ?";
    static final String PRE_STMT_2 = "SELECT DISTINCT c.CATEGORIEMERE FROM CATEGORIE c";
    static final String PRE_STMT_3 = "SELECT c.NOMCATEGORIE FROM CATEGORIE c WHERE c.NOMCATEGORIE <> c.CATEGORIEMERE";
    static final String PRE_STMT_4 = "SELECT cato FROM (SELECT AVG(compt), cato FROM(SELECT PRODUIT.IDPRODUIT, PRODUIT.NOMCATEGORIE as cato, COUNT(OFFRE.IDPRODUIT) as compt, PRODUIT.INTITULE as inti FROM  OFFRE, PRODUIT, CATEGORIE WHERE OFFRE.IDPRODUIT (+)= PRODUIT.IDPRODUIT AND PRODUIT.NOMCATEGORIE = CATEGORIE.NOMCATEGORIE GROUP BY PRODUIT.IDPRODUIT, PRODUIT.NOMCATEGORIE, PRODUIT.INTITULE) GROUP BY cato ORDER BY AVG(compt) DESC, cato ASC)";
    static final String PRE_STMT_5 = "SELECT PRODUIT.NOMCATEGORIE as cato, COUNT(OFFRE.IDPRODUIT) as compt FROM  OFFRE, PRODUIT, CATEGORIE WHERE OFFRE.IDPRODUIT (+)= PRODUIT.IDPRODUIT AND PRODUIT.NOMCATEGORIE = CATEGORIE.NOMCATEGORIE AND OFFRE.IDUTILISATEUR = ? AND PRODUIT.IDPRODUIT in (SELECT p.IDPRODUIT FROM PRODUIT p, OFFRE o MINUS SELECT distinct p.IDPRODUIT FROM PRODUIT p, OFFRE o WHERE o.ACHATREUSSI = 1 AND p.IDPRODUIT = o.IDPRODUIT AND o.IDUTILISATEUR = ?) GROUP BY PRODUIT.NOMCATEGORIE ORDER BY compt DESC, cato ASC";
    static final String PRE_STMT_6 = "SELECT INTITULE, DESCRIPT, NOMCATEGORIE, URLIMAGE, IDPRODUIT, PRIXCOURANT FROM PRODUIT WHERE IDPRODUIT = ?";
    static final String GET_PRIX_COURANT = "SELECT p.PRIXCOURANT FROM PRODUIT p WHERE p.IDPRODUIT = ?";
    static final String GET_NB_OFFRES = "SELECT COUNT(o.IDPRODUIT) FROM OFFRE o WHERE o.IDPRODUIT =?";
    static final String INSCRIPTION_COMPTE = "INSERT INTO Client Values(?, ?, ?, ?, ?, ?)";
    static final String AJOUTER_UTILISATEUR = "INSERT INTO UTILISATEUR Values(?)";
    static final String PROPOSITION_OFFRE = "INSERT INTO OFFRE VALUES (?, ?, ?, ?, ?)";
    static final String UPDATE_PRIX = "UPDATE PRODUIT SET PRIXCOURANT = ? WHERE IDPRODUIT = ?";
    static final String DELETE_USER = "DELETE FROM CLIENT WHERE IDUTILISATEUR = ?";
    static final String UPDATE_IDUSER = "UPDATE UTILISATEUR SET IDUTILISATEUR = ? WHERE IDUTILISATEUR = ?";
    static final String UPDATE_OFFRE_IDUSER = "UPDATE OFFRE SET IDUTILISATEUR = ? WHERE IDUTILISATEUR = ?";
    static final String CARACTERISTIQUE = "SELECT NOMCARACTERISTIQUE, VALEURCARACTERISTIQUE FROM CARACTERISTIQUE WHERE IDPRODUIT = ?";
    static final String PRODUITS_NON_VENDUS = "SELECT DISTINCT IDPRODUIT, INTITULE FROM PRODUIT MINUS SELECT DISTINCT PRODUIT.IDPRODUIT, INTITULE FROM PRODUIT, OFFRE WHERE PRODUIT.IDPRODUIT = OFFRE.IDPRODUIT AND OFFRE.ACHATREUSSI = 1";

    // fonction qui génére un entier entre deux bornes
    int genererInt(int borneInf, int borneSup) {
   Random random = new Random();
   int nb;
   nb = borneInf+random.nextInt(borneSup-borneInf);
   return nb;
}
    public Javasql() {
        try {
        	
	        // Enregistrement du driver Oracle
	        System.out.print("Loading Oracle driver... ");
	        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("loaded");

	        // Etablissement de la connection
	        System.out.print("Connecting to the database... ");
	        Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected");
          // Début des transactions
            conn.setAutoCommit(false);
            boolean connected = false;
            int idUser = -1;
            
            // connexion ou inscription
            Scanner myObj10 = new Scanner(System.in);
            System.out.println("\nBienvenu sur Gange \nTapez 1 si vous voulez vous connecter à votre compte \nTapez 2 si vous voulez créer un compte");
            int choix = myObj10.nextInt();
            System.out.println();
         
            // inscription
            if (choix == 2) {
              Scanner myObj13 = new Scanner(System.in);
              System.out.println("Veuillez entrer votre nom");
              String nom1 = myObj13.nextLine();
              Scanner myObj14 = new Scanner(System.in);
              System.out.println("Veuillez entrer votre prenom");
              String prenom1 = myObj14.nextLine();

              Scanner myObj15 = new Scanner(System.in);
              System.out.println("Veuillez entrer votre adresse postale");
              String adpostale = myObj15.nextLine();
              Scanner myObj11 = new Scanner(System.in);
              System.out.println("Veuillez entrer une adresse mail valide");
              String adressemail0 = myObj11.nextLine();
              Scanner myObj12 = new Scanner(System.in);
              System.out.println("Veuillez entrer un mot de passe");
              String mdp = myObj12.nextLine();
              System.out.println("\nvotre compte Gange a été bien créé\n");
              PreparedStatement inscrip = conn.prepareStatement(INSCRIPTION_COMPTE);
              int idutilis = genererInt(30,1000);
              inscrip.setString(1, nom1);
              inscrip.setString(2, prenom1);
              inscrip.setString(3, mdp);
              inscrip.setString(4, adressemail0);
              inscrip.setString(5, adpostale);
              inscrip.setInt(6, idutilis);
              ResultSet resinscrip = inscrip.executeQuery();
              resinscrip.close();
              inscrip.close();
              
              PreparedStatement addUtilisateur = conn.prepareStatement(AJOUTER_UTILISATEUR);
              addUtilisateur.setInt(1, idutilis);
              ResultSet raddUtilisateur = addUtilisateur.executeQuery();
              raddUtilisateur.close();
              addUtilisateur.close();
              
              conn.commit();

                }
            
            while(!connected) {

                Scanner myObj2 = new Scanner(System.in);
                System.out.println("Connexion à votre compte Gange \nAdresse mail :");
                String adresseMail = myObj2.nextLine();
                System.out.println();

                Scanner myObj3 = new Scanner(System.in);
                System.out.println("Quel est votre mot de passe ?");
                String motDePasseRentre = myObj3.nextLine();
                System.out.println();

                PreparedStatement stmt1 = conn.prepareStatement(PRE_STMT_1);
                stmt1.setString(1, adresseMail);
                ResultSet rset1 = stmt1.executeQuery();

                while (rset1.next()) {
                    String mdp = rset1.getString("MDPUTILISATEUR");
                    idUser = rset1.getInt("IDUTILISATEUR");
                    if (motDePasseRentre.equals(mdp)) {
                        System.out.println("Connexion au compte " + adresseMail.toString() + " réussie !\n");
                        connected = true;
                        rset1.close();
                        stmt1.close();
                        break;
                    }
                }
                if (!connected) {
                System.out.println("l'adresse mail ou le mot de passe est incorrect\n");
                rset1.close();
                stmt1.close();
                }
            }

            System.out.println("1. Parcours des catégories\n2. Parcours des sous-catégories\n3. Recommandations générales\n4. Recommandations personnalisées\n5. Consultation de la fiche d'un produit\n6. Proposer une offre\n7. Afficher les produits restants à la vente \n8. Oublier mon compte\n9. Fermer l'application Gange\n");
            System.out.println("Tapez le numéro correspondant à  l'action demandée\n");
            String instruction = "";
            while (!instruction.equals("9")) {
                Scanner myObj = new Scanner(System.in);
                System.out.println("Instruction demandée : ");
                instruction = myObj.nextLine();
                System.out.println();
                outerloop:
                switch(instruction) {
                    case "1":
                        PreparedStatement stmt2 = conn.prepareStatement(PRE_STMT_2);
                        ResultSet rset2 = stmt2.executeQuery();
                        dumpResultSet(rset2);
                        rset2.close();
                        stmt2.close();
                        break;

                    case "2":
                        PreparedStatement stmt3 = conn.prepareStatement(PRE_STMT_3);
                        ResultSet rset3 = stmt3.executeQuery();
                        dumpResultSet(rset3);
                        rset3.close();
                        stmt3.close();
                        break;

                    case "3":
                        PreparedStatement stmt4 = conn.prepareStatement(PRE_STMT_4);
                        ResultSet rset4 = stmt4.executeQuery();
                        dumpResultSet(rset4);
                        rset4.close();
                        stmt4.close();
                        break;

                    case "4":
                        if(idUser == 0) {
                            Scanner myObj4 = new Scanner(System.in);
                            System.out.println("Pour quel utilisateur souhaitez vous afficher les recommandations ? ");
                            int idUtilisateur = myObj4.nextInt();

                            PreparedStatement stmt5 = conn.prepareStatement(PRE_STMT_5);
                            stmt5.setInt(1, idUtilisateur);
                            stmt5.setInt(2, idUtilisateur);
                            ResultSet rset5 = stmt5.executeQuery();
                            dumpResultSet(rset5);
                            rset5.close();
                            stmt5.close();
                            break;
                        } else {
                            PreparedStatement stmt5 = conn.prepareStatement(PRE_STMT_5);
                            stmt5.setInt(1, idUser);
                            stmt5.setInt(2, idUser);
                            ResultSet rset5 = stmt5.executeQuery();
                            dumpResultSet(rset5);
                            rset5.close();
                            stmt5.close();
                            break;
                        }

                    case "5":
                        Scanner myObj5 = new Scanner(System.in);
                        System.out.println("Pour quel IDPRODUIT souhaitez vous afficher la fiche complète ? ");
                        int idProduit = myObj5.nextInt();

                        PreparedStatement stmt6 = conn.prepareStatement(PRE_STMT_6);
                        stmt6.setInt(1, idProduit);
                        ResultSet rset6 = stmt6.executeQuery();

                        PreparedStatement carac = conn.prepareStatement(CARACTERISTIQUE);
                        carac.setInt(1, idProduit);
                        ResultSet rcarac = carac.executeQuery();


                        while (rset6.next()) {
                            int idProd = rset6.getInt("IDPRODUIT");
                            String intitule = rset6.getString("INTITULE");
                            String description = rset6.getString("DESCRIPT");
                            String urlImage = rset6.getString("URLIMAGE");
                            float prixCourant = rset6.getFloat("PRIXCOURANT");
                            String nomCategorie = rset6.getString("NOMCATEGORIE");

                            System.out.println("IDPRODUIT : " + idProd + "\nINTITULE : " + intitule + "\nDESCRIPT : " + description + "\nURLIMAGE : " + urlImage + "\nPRIXCOURANT : " + prixCourant + " €\nNOMCATEGORIE : " + nomCategorie + "\n");

                            rset6.close();
                            stmt6.close();
                            System.out.println("Caractéristiques du produit " + idProduit + ":\n");
                            dumpResultSet(rcarac);
                            rcarac.close();
                            carac.close();
                            break outerloop;
                        }
                        System.out.println("Cet IDPRODUIT n'existe pas\n");
                        rset6.close();
                        stmt6.close();
                        rcarac.close();
                        carac.close();
                        break;
                        
                    case "6":
                        Scanner myObj6 = new Scanner(System.in);
                        System.out.println("Pour quel produit souhaitez vous faire une offre ?");
                        int idProd = myObj6.nextInt();

                        PreparedStatement prixCourant = conn.prepareStatement(GET_PRIX_COURANT);
                        prixCourant.setInt(1, idProd);
                        ResultSet rPrixCourant = prixCourant.executeQuery();
                        float prixCour = 0;
                        while (rPrixCourant.next()) {
                            prixCour = rPrixCourant.getFloat("PRIXCOURANT");
                        }
                        
                        if (prixCour == 0) {
                        	System.out.println("Cet IDPRODUIT n'existe pas\n");
                        	rPrixCourant.close();
                        	prixCourant.close();
                            break;
                        }
                        
                        PreparedStatement nbOffres = conn.prepareStatement(GET_NB_OFFRES);
                        nbOffres.setInt(1, idProd);
                        ResultSet rnbOffres = nbOffres.executeQuery();
                        while (rnbOffres.next()) {
                            int nombreOffres = rnbOffres.getInt("COUNT(o.IDPRODUIT)");
                            if (nombreOffres < NB_OFFRE_TO_BUY) {
                                Scanner myObj8 = new Scanner(System.in);
                                System.out.println("\nLe prix courant du produit est " + prixCour + " euros");
                                System.out.println("\nQuel prix proposez-vous pour ce produit ?");
                                float prixPropose = myObj8.nextFloat();

                                if (prixPropose > prixCour) {
                                    System.out.println("Offre acceptée !");

                                    PreparedStatement propositionOffre = conn.prepareStatement(PROPOSITION_OFFRE);
                                    propositionOffre.setTimestamp(1, (new Timestamp(System.currentTimeMillis())));
                                    propositionOffre.setInt(2, idProd);
                                    propositionOffre.setFloat(3, prixPropose);
                                    propositionOffre.setInt(4, idUser);
                                    if (nombreOffres == NB_OFFRE_TO_BUY - 1) {
                                        propositionOffre.setInt(5, 1);
                                        System.out.println("C'est vous qui obtenez le produit !\n");
                                    } else {
                                        propositionOffre.setInt(5, 0);
                                        System.out.println("Votre offre a bien été enregistrée \n");
                                    }
                                    ResultSet rpropositionOffre = propositionOffre.executeQuery();

                                    PreparedStatement updatePrix = conn.prepareStatement(UPDATE_PRIX);
                                    updatePrix.setFloat(1, prixPropose);
                                    updatePrix.setInt(2, idProd);
                                    ResultSet rupdatePrix = updatePrix.executeQuery();
                                    conn.commit(); // On commit l'offre et la mise à jour du prix en même temps
                                    break outerloop;
                                } else {
                                    System.out.println("\nErreur : le montant proposé est inférieur au prix courant du produit\n");
                                    break outerloop;
                                }
                                
                            }
                        }
                        System.out.println("Cet IDPRODUIT n'existe pas\n");
                        rnbOffres.close();
                        nbOffres.close();
                        break;
                                                                   
                    case "7":
                    	PreparedStatement produits_restants = conn.prepareStatement(PRODUITS_NON_VENDUS);
                        ResultSet rproduits_restants = produits_restants.executeQuery();
                        System.out.println("Voici les produits disponibles à la vente\n");
                        dumpResultSet(rproduits_restants);
                        produits_restants.close();
                        rproduits_restants.close();
                        break;
                        
                    case "8":
                        PreparedStatement oubli = conn.prepareStatement(DELETE_USER);
                        if (idUser == 0) {
                        	Scanner myObj11 = new Scanner(System.in);
                            System.out.println("Pour quel IDUTILISATEUR souhaitez-vous supprimer le compte ?");
                            int idUserToDelete = myObj11.nextInt();
                        	oubli.setInt(1, idUserToDelete);
                            ResultSet roubli = oubli.executeQuery();
                            System.out.println("Le compte " + idUserToDelete +" a bien été supprimé de Gange\n");
                            roubli.close();
                            oubli.close();
                            
                            int newIdUser = 1000 * idUserToDelete + 1001;
                            PreparedStatement updateUser = conn.prepareStatement(UPDATE_IDUSER);
                            updateUser.setInt(1, newIdUser);
                            updateUser.setInt(2, idUserToDelete);
                            ResultSet rupdateUser = updateUser.executeQuery();
                            rupdateUser.close();
                            updateUser.close();

                            PreparedStatement updateOffreUser = conn.prepareStatement(UPDATE_OFFRE_IDUSER);
                            updateOffreUser.setInt(1, newIdUser);
                            updateOffreUser.setInt(2, idUserToDelete);
                            ResultSet rupdateOffreUser = updateOffreUser.executeQuery();
                            
                            conn.commit(); // On commit la suppression du compte, la mise à jour du nouvel IDUTILISATEUR dans UTILISATEUR et dans OFFRE en même temps
                            rupdateOffreUser.close();
                            updateOffreUser.close();
                            instruction = "9";
                            break;
                        } else {
	                        oubli.setInt(1, idUser);
	                        ResultSet roubli = oubli.executeQuery();
	                        System.out.println("Votre compte a bien été supprimé de Gange\n");
	                        roubli.close();
	                        oubli.close();

	                        int newIdUser = 1000 * idUser + 1001;
	                        PreparedStatement updateUser = conn.prepareStatement(UPDATE_IDUSER);
	                        updateUser.setInt(1, newIdUser);
	                        updateUser.setInt(2, idUser);
	                        ResultSet rupdateUser = updateUser.executeQuery();
	                        rupdateUser.close();
	                        updateUser.close();
	
	                        PreparedStatement updateOffreUser = conn.prepareStatement(UPDATE_OFFRE_IDUSER);
	                        updateOffreUser.setInt(1, newIdUser);
	                        updateOffreUser.setInt(2, idUser);
	                        ResultSet rupdateOffreUser = updateOffreUser.executeQuery();
	                        
	                        conn.commit(); // On commit la suppression du compte, la mise à jour du nouvel IDUTILISATEUR dans UTILISATEUR et dans OFFRE en même temps
	                        rupdateOffreUser.close();
	                        updateOffreUser.close();
	                        instruction = "9";
	                        break;
                        }
                        
                    default:
                        if (!instruction.equals("9")) {
                            System.out.println("L'instruction " + instruction.toString() + " est incorrecte\n");
                        }
                }
            }

        conn.close();

        } catch (SQLException e) {
            System.err.println("failed");
            e.printStackTrace(System.err);
        }
    }

    private void dumpResultSet(ResultSet rset) throws SQLException {
        ResultSetMetaData rsetmd = rset.getMetaData();
        int i = rsetmd.getColumnCount();
        while (rset.next()) {
            for (int j = 1; j <= i; j++) {
                System.out.print(rset.getString(j) + "\t");
	    }
	    System.out.println();
        }
        System.out.println();
    }

    public static void main(String args[]) {
        new Javasql();
    }
}