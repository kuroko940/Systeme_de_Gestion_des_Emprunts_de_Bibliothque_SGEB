package service;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Adherent;
import model.Document;
import model.Emprunt;
import model.Livre;
import model.Magazine;

/*
DatabaseManager gère la connexion entre Java et SQLite.
Il permet d’enregistrer et relire les données dans un fichier bibliotheque.db.
Comme ça, mes documents et adhérents ne sont pas perdus quand je quitte le programme.
 */

public class DatabaseManager {
	private static final String DB_URL = "jdbc:sqlite:bibliotheque.db";
    private Connection connection;

    // Ouvre la connexion à la base de données
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connexion à la base SQLite réussie !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base : " + e.getMessage());
        }
    }

    public void createTables() {
    	String sqlDocuments = "CREATE TABLE IF NOT EXISTS documents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titre TEXT NOT NULL," +
                "auteur TEXT NOT NULL," +
                "genre TEXT," +
                "disponible BOOLEAN DEFAULT 1," +
                "type TEXT DEFAULT 'Livre'," +
                "isbn TEXT," +           
                "nb_pages INTEGER," +    
                "numero INTEGER," +      
                "periodicite INTEGER" +  
                ");";

        String sqlAdherents = "CREATE TABLE IF NOT EXISTS adherents (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "penalite BOOLEAN DEFAULT 0" +
                ");";
        
        String sqlEmprunts = "CREATE TABLE IF NOT EXISTS emprunts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "document_id INTEGER NOT NULL," +
                "adherent_id INTEGER NOT NULL," +
                "date_emprunt TEXT NOT NULL," +
                "date_retour_prevue TEXT NOT NULL," +
                "date_retour_reelle TEXT," +
                "penalite REAL DEFAULT 0," +
                "FOREIGN KEY(document_id) REFERENCES documents(id)," +
                "FOREIGN KEY(adherent_id) REFERENCES adherents(id)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlDocuments);
            System.out.println("Table documents créée");
            
            stmt.execute(sqlAdherents);
            System.out.println("Table adherents créée");
            
            stmt.execute(sqlEmprunts);
            System.out.println("Table emprunts créée");
            
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables : " + e.getMessage());
            e.printStackTrace();  // 
        }
    }


  public Integer insertDocument(Document d) {
     String sql = "INSERT INTO documents(titre, auteur, genre, disponible, type, isbn, nb_pages, numero, periodicite) " +
                  "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
     
     try (var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         ps.setString(1, d.getTitreDocument());
         ps.setString(2, d.getAuteur());
         
         // Gestion du genre null
         String genre = (d.getGenre() == null) ? "Inconnu" : d.getGenre();
         ps.setString(3, genre);
         
         ps.setBoolean(4, d.getStatut());

         if (d instanceof Livre) {
             Livre l = (Livre) d;
             ps.setString(5, "Livre");
             ps.setString(6, l.getIsbn());
             // Si nbPages est null, on met 0
             ps.setInt(7, (l.getNbPages() != null) ? l.getNbPages() : 0);
             ps.setNull(8, java.sql.Types.INTEGER); // Pas de numero
             ps.setNull(9, java.sql.Types.INTEGER); // Pas de periodicite
         } else if (d instanceof Magazine) {
             Magazine m = (Magazine) d;
             ps.setString(5, "Magazine");
             ps.setNull(6, java.sql.Types.VARCHAR); // Pas d'ISBN
             ps.setNull(7, java.sql.Types.INTEGER); // Pas de nbPages
             ps.setInt(8, m.getNumero());
             ps.setInt(9, m.getPeriodicite());
         } else {
             // Cas par defaut 
             ps.setString(5, "Autre");
             ps.setNull(6, java.sql.Types.VARCHAR);
             ps.setNull(7, java.sql.Types.INTEGER);
             ps.setNull(8, java.sql.Types.INTEGER);
             ps.setNull(9, java.sql.Types.INTEGER);
         }

         ps.executeUpdate();
         
         //Partie style 
         var rs = ps.getGeneratedKeys();
         if (rs.next()) {
             int id = rs.getInt(1);
             System.out.println("Document inséré : " + d.getTitreDocument() + " (ID: " + id + ")");
             return id;
         }
     } catch (Exception e) {
         System.out.println("Erreur insertDocument: " + e.getMessage());
     }
     return null;
 }

 
    public Integer insertAdherent(String nom, String prenom, String email, boolean penalite) {
        String sql = "INSERT INTO adherents(nom, prenom, email, penalite) VALUES(?, ?, ?, ?)";
        try (var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setBoolean(4, penalite);
            ps.executeUpdate();
            
            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("Adhérent inséré : " + nom + " " + prenom + " (ID: " + id + ")");
                return id;
            }
        } catch (Exception e) {
            System.out.println("insertAdherent: " + e.getMessage());
        }
        return null;
    }
   
    
 // Ferme la connexion
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture : " + e.getMessage());
        }
    }
    
    public void testConnection() {
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");
            System.out.println("\n Tables présentes dans la base :");
            while (rs.next()) {
                System.out.println(" - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur de test : " + e.getMessage());
        }
    }
    
    public void afficherTousLesDocuments() {
        String sql = "SELECT id, titre, auteur, genre, disponible FROM documents";
        try (var st = connection.createStatement(); var rs = st.executeQuery(sql)) {
            System.out.println("\nDocuments en base :");
            boolean vide = true;
            while (rs.next()) {
                vide = false;
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("titre") + " - " +
                    rs.getString("auteur") + " (" +
                    rs.getString("genre") + ") | Disponible: " +
                    rs.getBoolean("disponible"));
            }
            if (vide) System.out.println("(aucun)");
        } catch (Exception e) {
            System.out.println("afficherTousLesDocuments: " + e.getMessage());
        }
    }

    public void afficherTousLesAdherents() {
        String sql = "SELECT id, nom, prenom, email, penalite FROM adherents";
        try (var st = connection.createStatement(); var rs = st.executeQuery(sql)) {
            System.out.println("\nAdhérents en base :");
            boolean vide = true;
            while (rs.next()) {
                vide = false;
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("nom") + " " +
                    rs.getString("prenom") + " (" +
                    rs.getString("email") + ") | Pénalité: " +
                    rs.getBoolean("penalite"));
            }
            if (vide) System.out.println("(aucun)");
        } catch (Exception e) {
            System.out.println("afficherTousLesAdherents: " + e.getMessage());
        }
    }
    
    
    public List<Document> chargerTousLesDocuments() {
        List<Document> liste = new ArrayList<>();
        // On sélectionne tout (*) pour avoir isbn, numero, etc.
        String sql = "SELECT * FROM documents"; 

        try (var st = connection.createStatement();
             var rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String titre = rs.getString("titre");
                String auteur = rs.getString("auteur");
                String genre = rs.getString("genre");
                boolean dispo = rs.getBoolean("disponible");
                String type = rs.getString("type");

                Document doc;
                
                if ("Magazine".equals(type)) {
                    // Récupération des champs spécifiques de Magazine
                    int numero = rs.getInt("numero");
                    int periodicite = rs.getInt("periodicite");
                    doc = new Magazine(titre, auteur, numero, periodicite);
                } else {
                    // Par defaut, on considère que c'est un Livre
                    String isbn = rs.getString("isbn");
                    int nbPages = rs.getInt("nb_pages");
                    doc = new Livre(titre, auteur, isbn, nbPages);
                }
                
                // Paramètres communs
                doc.setId(id);
                doc.setGenre(genre);
                doc.setStatutDocument(dispo);

                liste.add(doc);
            }
        } catch (Exception e) {
            System.out.println("Erreur chargerTousLesDocuments: " + e.getMessage());
        }
        return liste;
    }
    
    

 // Recupere tous les adherents de la base et les retourne sous forme de liste
    public List<Adherent> chargerTousLesAdherents() {
        List<Adherent> liste = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, penalite FROM adherents";
        try (var st = connection.createStatement();
             var rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                boolean penalite = rs.getBoolean("penalite");

                Adherent a = new Adherent(nom, prenom, email);
                try { a.setId(id); } catch (Exception ignore) {}
                a.setPenalite(penalite);

                liste.add(a);
            }
        } catch (Exception e) {
            System.out.println("chargerTousLesAdherents: " + e.getMessage());
        }
        return liste;
    }
    
    
    public List<Emprunt> chargerTousLesEmprunts(List<Document> documents, List<Adherent> adherents) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts";

        try (var stmt = connection.createStatement(); var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int docId = rs.getInt("document_id");
                int adhId = rs.getInt("adherent_id");
                
                LocalDate dateEmprunt = LocalDate.parse(rs.getString("date_emprunt"));
                LocalDate dateRetourPrevue = LocalDate.parse(rs.getString("date_retour_prevue"));
                String retourStr = rs.getString("date_retour_reelle");
                LocalDate dateRetourReelle = (retourStr != null) ? LocalDate.parse(retourStr) : null;
      
                Document doc = documents.stream().filter(d -> d.getId() == docId).findFirst().orElse(null);
                Adherent adh = adherents.stream().filter(a -> a.getId() == adhId).findFirst().orElse(null);

                if (doc != null && adh != null) {        	
                	Emprunt e = new Emprunt(doc, adh, dateEmprunt);
                	e.setDateRetourPrevue(dateRetourPrevue);
                	if (dateRetourReelle != null) {
                	    e.setDateRetourReelle(dateRetourReelle);
                	}
                	emprunts.add(e);      
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des emprunts : " + e.getMessage());
        }
        
        return emprunts;
    }

    
    public void insertEmprunt(int documentId, int adherentId, LocalDate dateEmprunt, LocalDate dateRetourPrevue) {
        String sql = "INSERT INTO emprunts(document_id, adherent_id, date_emprunt, date_retour_prevue) VALUES (?, ?, ?, ?)";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, documentId);
            pstmt.setInt(2, adherentId);
            pstmt.setString(3, dateEmprunt.toString());
            pstmt.setString(4, dateRetourPrevue.toString());
            pstmt.executeUpdate();
            System.out.println("Emprunt inséré avec date prévue : " + dateRetourPrevue);
            System.out.println("Emprunt ajouté à la base (Doc " + documentId + ", Adh " + adherentId + ")");
        } catch (Exception e) {
            System.out.println("insertEmprunt: " + e.getMessage());
        }
    }
    
    
    public void updateAdherent(Adherent a) {
        if (a == null) {
            System.out.println("Adhérent nul, mise à jour impossible.");
            return;
        }

        String sql = "UPDATE adherents SET nom = ?, prenom = ?, email = ?, penalite = ? WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getEmail());
            ps.setBoolean(4, a.getStatut());
            ps.setInt(5, a.getId());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Adhérent mis à jour dans la base : " + a.getNom() + " " + a.getPrenom());
            } else {
                System.out.println("Aucun adhérent trouvé avec l’ID : " + a.getId());
            }
        } catch (Exception e) {
            System.out.println("Erreur updateAdherent : " + e.getMessage());
        }
    }
    
    public void deleteAdherent(String nom, String prenom) {
        String sql = "DELETE FROM adherents WHERE nom = ? AND prenom = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Adhérent supprimé de la base : " + nom + " " + prenom);
            } else {
                System.out.println("Aucun adhérent trouvé avec ce nom et prénom dans la base.");
            }
        } catch (Exception e) {
            System.out.println("Erreur deleteAdherent : " + e.getMessage());
        }
    }


    public void updateDocumentDisponibilite(int documentId, boolean disponible) {
        String sql = "UPDATE documents SET disponible = ? WHERE id = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, documentId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("updateDocumentDisponibilite: " + e.getMessage());
        }
    }

    public Integer findDocumentIdByTitleAuthor(String titre, String auteur) {
        String sql = "SELECT id FROM documents WHERE titre = ? AND auteur = ? LIMIT 1";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setString(2, auteur);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            System.out.println("findDocumentId: " + e.getMessage());
        }
        return null;
    }

    public Integer findAdherentIdByEmail(String email) {
        String sql = "SELECT id FROM adherents WHERE email = ? LIMIT 1";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            System.out.println("findAdherentId: " + e.getMessage());
        }
        return null;
    }
    
    public void enregistrerRetour(int documentId, int adherentId, LocalDate dateRetourReelle, double penalite) {
        String sql = "UPDATE emprunts SET date_retour_reelle = ?, penalite = ? " +
                     "WHERE document_id = ? AND adherent_id = ? AND date_retour_reelle IS NULL";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, dateRetourReelle.toString());
            ps.setDouble(2, penalite);
            ps.setInt(3, documentId);
            ps.setInt(4, adherentId);
            ps.executeUpdate();
            System.out.println("Retour enregistré (doc " + documentId + ", adh " + adherentId + ")");
        } catch (Exception e) {
            System.out.println("enregistrerRetour: " + e.getMessage());
        }
    }
    
 

    // Alertes de retard
    public void afficherEmpruntsEnRetard() {
        String sql = "SELECT d.titre, a.nom, a.prenom, e.date_retour_prevue " +
                     "FROM emprunts e " +
                     "JOIN documents d ON e.document_id = d.id " +
                     "JOIN adherents a ON e.adherent_id = a.id " +
                     "WHERE e.date_retour_reelle IS NULL " +
                     "AND date(e.date_retour_prevue) < date('now')";
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {

            System.out.println("\nEmprunts en retard :");
            boolean vide = true;
            while (rs.next()) {
                vide = false;
                System.out.println("⚠️ " + rs.getString("titre") +
                        " | " + rs.getString("nom") + " " + rs.getString("prenom") +
                        " (retour prévu le " + rs.getString("date_retour_prevue") + ")");
            }
            if (vide) System.out.println("(aucun)");
        } catch (Exception e) {
            System.out.println("afficherEmpruntsEnRetard: " + e.getMessage());
        }
    }
    
    public void deleteDocument(String titre, String auteur) {
        String sql = "DELETE FROM documents WHERE titre = ? AND auteur = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setString(2, auteur);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Document supprimé de la base : " + titre + " (" + auteur + ")");
            } else {
                System.out.println("Aucun document trouvé avec ce titre/auteur dans la base.");
            }
        } catch (Exception e) {
            System.out.println("Erreur deleteDocument : " + e.getMessage());
        }
    }
    

    public void updateDocument(Document d) {
        if (d == null) return;

        // 1. La requete SQL met a jour toutes les colonnes
        String sql = "UPDATE documents SET titre=?, auteur=?, genre=?, disponible=?, type=?, isbn=?, nb_pages=?, numero=?, periodicite=? WHERE id=?";
        
        try (var ps = connection.prepareStatement(sql)) {
            // Infos de base
            ps.setString(1, d.getTitreDocument());
            ps.setString(2, d.getAuteur());
            ps.setString(3, d.getGenre());
            ps.setBoolean(4, d.getStatut());

            // Infos Specifiques
            if (d instanceof Livre) {
                Livre l = (Livre) d;
                ps.setString(5, "Livre");
                ps.setString(6, l.getIsbn());
                ps.setObject(7, l.getNbPages()); // setObject gere les nombres vides
                
                ps.setObject(8, null); // Pas de numero
                ps.setObject(9, null); // Pas de periodicite

            } else if (d instanceof Magazine) {
                Magazine m = (Magazine) d;
                ps.setString(5, "Magazine");
                ps.setObject(6, null); // Pas d'ISBN
                ps.setObject(7, null); // Pas de pages
                
                ps.setInt(8, m.getNumero());
                ps.setInt(9, m.getPeriodicite());
            }

            ps.setInt(10, d.getId()); 

            ps.executeUpdate();
            System.out.println("Document mis à jour en base (ID: " + d.getId() + ")");

        } catch (Exception e) {
            System.out.println("Erreur updateDocument : " + e.getMessage());
        }
    }

    
    public List<Emprunt> getHistoriqueAdherent(String email) {
        List<Emprunt> historique = new ArrayList<>();

        String sql = """
            SELECT d.titre, d.auteur, e.date_emprunt, e.date_retour_prevue, e.date_retour_reelle
            FROM emprunts e
            JOIN adherents a ON e.adherent_id = a.id
            JOIN documents d ON e.document_id = d.id
            WHERE a.email = ?
            ORDER BY e.date_emprunt DESC
        """;

        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            var rs = ps.executeQuery();

            while (rs.next()) {
                // Creation d’un document pour affichage historique
            	Document doc = new Livre(rs.getString("titre"),rs.getString("auteur"));

                LocalDate dateEmprunt = LocalDate.parse(rs.getString("date_emprunt"));
                LocalDate dateRetourPrevue = LocalDate.parse(rs.getString("date_retour_prevue"));

                Emprunt e = new Emprunt(doc, null, dateEmprunt);
                e.setDateRetourPrevue(dateRetourPrevue);

                String retourReelle = rs.getString("date_retour_reelle");
                if (retourReelle != null) {
                    e.setDateRetourReelle(LocalDate.parse(retourReelle));
                }

                historique.add(e);
            }
        } catch (Exception e) {
            System.out.println("Erreur getHistoriqueAdherent : " + e.getMessage());
        }

        return historique;
    }

    
   
}
