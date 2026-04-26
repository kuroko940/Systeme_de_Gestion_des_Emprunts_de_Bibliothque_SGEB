package service;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import model.Adherent;
import model.Document;
import model.Emprunt;
import model.Livre;
import model.Magazine;

public class BibliothequeManager {
	
	// Stockage en mémoire 
	private List<Adherent> adherents;
	private List<Document> documents;
	private List<Emprunt> emprunts;
	private DatabaseManager db;

	public BibliothequeManager(DatabaseManager db) {
	    this.db = db;
	    this.documents = db.chargerTousLesDocuments(); 
	    this.adherents = db.chargerTousLesAdherents();
	    this.emprunts = db.chargerTousLesEmprunts(documents, adherents);

	    System.out.println("Documents chargés: " + documents.size());
	    System.out.println("Adhérents chargés: " + adherents.size());
	    System.out.println("Emprunts chargés : " + emprunts.size());
	}

	 
	public List<Emprunt> getEmprunts() {
		return emprunts;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	
	public List<Adherent> getAdherents() {
		return adherents;
	}
	
	public DatabaseManager getDb() {
		return db;
	}
	
	 
	public Adherent chercherAdherentParEmail(String email) {
		if (email == null || email.isBlank()) return null;
		   for (Adherent a : adherents) {
		       if (a.getEmail().equalsIgnoreCase(email)) {
		           return a;
		       }
		   }
		   // Si aucun trouvé
		   return null;
		}
	
	
	

	public void ajouterAdherent(Adherent a) throws BibliothequeException {
	    if (a == null) {
	        throw new IllegalArgumentException("L'adhérent fourni est invalide (null).");
	    }
	    // 2. Vérification Doublon
	    for (Adherent existant : adherents) {
	        if (existant.getEmail() != null && existant.getEmail().equalsIgnoreCase(a.getEmail())) {
	            throw new BibliothequeException("Un adhérent avec cet email existe déjà : " + a.getEmail());
	        }
	    }
	    //insertion en base
	    try {
	        Integer id = db.insertAdherent(a.getNom(), a.getPrenom(), a.getEmail(), a.getStatut());	        
	        if (id != null) {
	            // Succès : on met à jour l'objet et la liste
	            a.setId(id);
	            adherents.add(a);
	            // On ne retourne rien. Si on arrive ici, c'est que tout va bien.
	        } else {
	            // Echec de recuperation de l'ID
	            throw new BibliothequeException("Impossible de récupérer l'ID généré par la base de données.");
	        }
	    } catch (Exception e) {
	        // Autre type d'erreur
	        throw new BibliothequeException("Erreur technique lors de l'enregistrement : " + e.getMessage());
	    }
	}


	public void modifierAdherent(Adherent a, String nouveauNom, String nouveauPrenom, String nouvelEmail, Boolean nouveauStatut) 
	                             throws BibliothequeException { 
	    if (a == null) {
	        throw new IllegalArgumentException("Adhérent invalide (null).");
	    }

	    boolean modifie = false;
	    
	    // verification du doublons d'email
	    if (nouvelEmail != null && !nouvelEmail.isBlank() && !nouvelEmail.equalsIgnoreCase(a.getEmail())) {
	        // On vérifie si un autre adhérent a deja cet email
	        for (Adherent autreAdherent : adherents) {
	            if (autreAdherent != a && autreAdherent.getEmail().equalsIgnoreCase(nouvelEmail)) {
	                // si erreur -> exception
	                throw new BibliothequeException("Impossible de modifier : L'email '" + nouvelEmail + "' est déjà utilisé.");
	            }
	        }
	        // Si pas de doublon, on applique
	        a.setEmail(nouvelEmail);
	        modifie = true;
	    }

	    //Modifications simples
	    if (nouveauNom != null && !nouveauNom.isBlank() && !nouveauNom.equalsIgnoreCase(a.getNom())) {
	        a.setNom(nouveauNom);
	        modifie = true;
	    }
	    if (nouveauPrenom != null && !nouveauPrenom.isBlank() && !nouveauPrenom.equalsIgnoreCase(a.getPrenom())) {
	        a.setPrenom(nouveauPrenom);
	        modifie = true;
	    }
	    if (nouveauStatut != null && nouveauStatut != a.getStatut()) {
	        a.setPenalite(nouveauStatut);
	        modifie = true;
	    }

	    // enregistrement final
	    if (modifie) {
	        try {
	            db.updateAdherent(a);
	            // Tout s'est bien passé, la méthode se termine normalement (void)
	        } catch (Exception e) {
	            throw new BibliothequeException("Erreur technique lors de la mise à jour en base : " + e.getMessage());
	        }
	    } else {
	        // Cas particulier : Rien n'a changé.
	        throw new BibliothequeException("Aucune modification n'a été détectée.");
	    }
	}

	public void supprimerAdherent(Adherent a) {
	    if (a == null) {
	        System.out.println("Adhérent invalide !");
	        return;
	    }

	    if (!adherents.contains(a)) {
	        System.out.println("Cet adhérent n’existe pas dans la base.");
	        return;
	    }

	    // Vérifier s'il a des emprunts en cours
	    for (Emprunt e : emprunts) {
	        if (e.getAdherent() == a && e.getDateRetourReelle() == null) {
	            System.out.println("Impossible de supprimer " + a.getNom() + " " + a.getPrenom() + " : emprunt(s) en cours !");
	            return;
	        }
	    }
	   
	    // Supprimer dans la base et en mémoire
	    db.deleteAdherent(a.getNom(), a.getPrenom());
	    adherents.remove(a);
	    System.out.println("L’adhérent " + a.getNom() + " " + a.getPrenom() + " a été supprimé avec succès de la memoire et base.");
	}

	
	public String ajouterDocument(Document d) {
	    if (d == null) {
	        return "Document invalide (null).";
	    }

	    // Vérifier s'il est déjà présent
	    for (Document existant : documents) {
	        if (existant.getTitreDocument().equalsIgnoreCase(d.getTitreDocument())
	            && existant.getAuteur().equalsIgnoreCase(d.getAuteur())) {
	            return "Ce document existe déjà dans le catalogue : '" + d.getTitreDocument() + "' de " + d.getAuteur();
	        }
	    }

	    try {        	        
	        Integer id = db.insertDocument(d);
	        
	        if (id != null) {
	            d.setId(id);  // 
	            documents.add(d);  // 
	            System.out.println("Document ajouté en mémoire avec ID : " + id);
	            return "✅ Document ajouté avec succès : '" + d.getTitreDocument() + "' de " + d.getAuteur();
	        } else {
	            return "Erreur : impossible de récupérer l'ID du document.";
	        }
	        
	    } catch (Exception e) {
	        return "Erreur lors de l'ajout en base : " + e.getMessage();
	    }
	}

	//supprimer un document
	public void supprimerDocument(Document d) {
	    if (d == null) {
	        System.out.println("Document invalide !");
	        return;
	    }
	    if (!documents.contains(d)) {
	        System.out.println("Ce document n’existe pas dans le catalogue.");
	        return;
	    }
	    if (!d.getStatut()) {
	        System.out.println("Impossible de supprimer : le document '" + d.getTitreDocument() + "' est actuellement emprunté.");
	        return;
	    }
	    
	    // Supprimer dans la base
	    db.deleteDocument(d.getTitreDocument(), d.getAuteur());
	    System.out.println("Le document '" + d.getTitreDocument() + "' a été supprimé de la base.");

	    //supp en memoire
	    documents.remove(d);
	    System.out.println("Le document '" + d.getTitreDocument() + "' a été supprimé de la memoire.");
	}

	
	public void modifierDocument(Document d, String nouveauTitre, String nouvelAuteur, String nouveauGenre, Boolean nouveauStatut, 
	                             String nouvelIsbn, Integer nouvellesPages, Integer nouveauNumero, Integer nouvellePeriodicite) 
	                             throws BibliothequeException { 
	    
	    if (d == null) throw new BibliothequeException("Document introuvable.");

	    // 1. Valeurs finales
	    String titreFinal = (nouveauTitre != null && !nouveauTitre.isBlank()) ? nouveauTitre : d.getTitreDocument();
	    String auteurFinal = (nouvelAuteur != null && !nouvelAuteur.isBlank()) ? nouvelAuteur : d.getAuteur();

	    // 2. on verifie, si doublons envoie exception
	    for (Document autre : documents) {
	        if (autre != d) {
	            if (autre.getTitreDocument().equalsIgnoreCase(titreFinal) 
	                && autre.getAuteur().equalsIgnoreCase(auteurFinal)) {
	               
	                throw new BibliothequeException("Le document '" + titreFinal + "' existe déjà dans la bibliothèque.");
	            }
	        }
	    }

	    boolean modifie = false;
	    
	    if (!titreFinal.equals(d.getTitreDocument())) {
	        d.setTitreDocument(titreFinal);
	        modifie = true;
	    }
	    if (!auteurFinal.equals(d.getAuteur())) {
	        d.setAuteur(auteurFinal);
	        modifie = true;
	    }
	    if (nouveauGenre != null && !nouveauGenre.equals(d.getGenre())) {
	        d.setGenre(nouveauGenre);
	        modifie = true;
	    }
	    if (nouveauStatut != null && nouveauStatut != d.getStatut()) {
	        d.setStatutDocument(nouveauStatut);
	        modifie = true;
	    }

	    //  Bloc Spécifique (Livre / Magazine) 
	    if (d instanceof Livre) {
	        Livre l = (Livre) d;
	        if (nouvelIsbn != null && !nouvelIsbn.isBlank()) {
	            l.setIsbn(nouvelIsbn);
	            modifie = true;
	        }
	        if (nouvellesPages != null && nouvellesPages > 0) {
	            l.setNbPages(nouvellesPages);
	            modifie = true;
	        }
	    }
	    else if (d instanceof Magazine) {
	        Magazine m = (Magazine) d;
	        if (nouveauNumero != null && nouveauNumero > 0) {
	            m.setNumero(nouveauNumero);
	            modifie = true;
	        }
	        if (nouvellePeriodicite != null && nouvellePeriodicite > 0) {
	            m.setPeriodicite(nouvellePeriodicite);
	            modifie = true;
	        }
	    }
	    
	    // Enregistrement final
	    if (modifie) {
	        try {
	            db.updateDocument(d);	            
	        } catch (Exception e) {
	            throw new BibliothequeException("Erreur base de données : " + e.getMessage());
	        }
	    } else {
	        throw new BibliothequeException("Aucune modification n'a été détectée.");
	    }
	}

	
	
	public List<Document> chercherDocumentsParTitre(String titre) {
	    List<Document> doc = new ArrayList<>();

	    if (titre == null || titre.isBlank()) return doc;

	    for (Document d : documents) {
	        if (d.getTitreDocument().equalsIgnoreCase(titre)) {
	            doc.add(d);
	        }
	    }
	    return doc;
	}
	

	public List<Document> chercherDocumentsParAuteur(String auteur) {
	    List<Document> doc = new ArrayList<>();

	    if (auteur == null || auteur.isBlank()) return doc;
	    for (Document d : documents) {
	        if (d.getAuteur().toLowerCase().equalsIgnoreCase(auteur)) {
	            doc.add(d);
	        }
	    }
	    return doc;
	}

	
	
	public List<Document> chercherDocumentParGenre(String genre){
		List<Document> doc = new ArrayList<>();

	    if (genre == null || genre.isBlank()) return doc;
	    for (Document d : documents) {
	        if (d.getGenre() != null && d.getGenre().toLowerCase().equalsIgnoreCase(genre)) {
	            doc.add(d);
	        }
	    }
	    return doc;
		
	}
	

	
	public Document chercherDocumentParTitreUnique(String titre) {
	    if (titre == null || titre.isBlank()) {
	        System.out.println("Titre invalide !");
	        return null;
	    }
	    for (Document d : documents) {
	        if (d.getTitreDocument().equalsIgnoreCase(titre)) {
	            return d; // On retourne le premier document trouvé
	        }
	    }
	    System.out.println("Aucun document trouvé avec ce titre");
	    return null;
	}
	
	public Document chercherDocumentParTitreEtAuteur(String titre, String auteur) {
	    if (titre == null || auteur == null) return null;
	    for (Document d : documents) {
	        if (d.getTitreDocument().equalsIgnoreCase(titre)
	                && d.getAuteur().equalsIgnoreCase(auteur)) {
	            return d;
	        }
	    }
	    return null;
	}


	

	public void emprunterDocument(Document d, Adherent a) throws BibliothequeException {
	    if (d == null || a == null) {
	        throw new IllegalArgumentException("Document ou adhérent non valide (null).");
	    }

	    boolean trouve = false;

	    for (Document o : documents) {
	        // On cherche le document dans la bibliotheque
	        if (o.getTitreDocument().equalsIgnoreCase(d.getTitreDocument())
	            && o.getAuteur().equalsIgnoreCase(d.getAuteur())) {
	            trouve = true;
	            // Vérifier si le document est disponible
	            if (!o.getStatut()) {
	                throw new BibliothequeException("Ce document est déjà emprunté !");
	            }
	            // Vérifier si l'adherent a une penalite
	            if (a.getStatut()) {
	                throw new BibliothequeException(a.getNom() + " " + a.getPrenom() + " a une pénalité active, il ne peut pas emprunter.");
	            }
	            // Vérifier le nombre d'emprunts en cours
	            int nbEmpruntsActuels = 0;
	            for (Emprunt e : emprunts) {
	                if (e != null && e.getAdherent() == a && e.getDateRetourReelle() == null) {
	                    nbEmpruntsActuels++;
	                }
	            }
	            
	            if (nbEmpruntsActuels >= 5) {
	                throw new BibliothequeException(a.getNom() + " a déjà atteint la limite d'emprunts autorisés (5).");
	            }

	            // Tout est bon donc on fait l'emprunt
	            Emprunt e = new Emprunt(d, a, LocalDate.now());
	            
	            // Calcul date retour 
	            if (e.getDateRetourPrevue() == null) {
	                 e.setDateRetourPrevue(e.getDateEmprunt().plusWeeks(3));
	            }

	            try {
	                // Mise à jour BDD
	                Integer docId = db.findDocumentIdByTitleAuthor(d.getTitreDocument(), d.getAuteur());
	                Integer adhId = db.findAdherentIdByEmail(a.getEmail());
	                
	                if (docId != null && adhId != null) {
	                    db.insertEmprunt(docId, adhId, e.getDateEmprunt(), e.getDateRetourPrevue());
	                    db.updateDocumentDisponibilite(docId, false);
	                } else {
	                    throw new BibliothequeException("Impossible d'enregistrer l'emprunt (ID manquant en base).");
	                }
	                
	                // Si BDD ok, on met à jour la memoire
	                emprunts.add(e);
	                d.setStatutDocument(false); // Le doc passe en 'non disponible'
	                
	            } catch (Exception ex) {
	                // En cas d'erreur technique
	                throw new BibliothequeException("Erreur persistance emprunt : " + ex.getMessage());
	            }           
	            // Si on est arrive la, c'est fini. On sort de la méthode.
	            return; 
	        }
	    }
	    // Si on sort de la boucle sans avoir trouvé le document
	    if (!trouve) {
	        throw new BibliothequeException("Le document '" + d.getTitreDocument() + "' n'existe pas dans la bibliothèque.");
	    }
	}

	
	public String retourDocument(Document d, Adherent a) {
	    if (d == null || a == null) {
	        return "Document ou adhérent non valide !";
	    }

	    // Chercher l'emprunt correspondant
	    Emprunt empruntTrouve = null;
	    for (Emprunt e : emprunts) {
	        if (e != null && e.getDocument() == d && e.getAdherent() == a && e.getDateRetourReelle() == null) {
	            empruntTrouve = e;
	            break;
	        }
	    }
	    if (empruntTrouve == null) {
	        return "Aucun emprunt en cours trouvé pour ce document ou cet adhérent.";
	    }

	    // Calcul du retour
	    double penalite = empruntTrouve.retour(LocalDate.now());

	    LocalDate dateRetour = LocalDate.now();
	    d.setStatutDocument(true);
	    empruntTrouve.setDateRetourReelle(dateRetour);

	    String message;

	    if (penalite > 0.0) {
	        a.setPenalite(true);
	        message = "Retard détecté : pénalité de " + String.format("%.2f", penalite)
	                + " € appliquée à " + a.getPrenom() + " " + a.getNom() + ".";
	    } else {
	        message = "Retour dans les temps du document '" + d.getTitreDocument()
	                + "'. Merci " + a.getPrenom() + " " + a.getNom() + " !";
	    }

	    message += "\nLe document '" + d.getTitreDocument() + "' de " + d.getAuteur() + " est maintenant disponible.";

	    // Persistance
	    try {
	        Integer docId = db.findDocumentIdByTitleAuthor(d.getTitreDocument(), d.getAuteur());
	        Integer adhId = db.findAdherentIdByEmail(a.getEmail());

	        if (docId != null && adhId != null) {
	            db.enregistrerRetour(docId, adhId, dateRetour, penalite);
	            db.updateDocumentDisponibilite(docId, true);
	        } else {
	            message += "\nErreur : impossible d'enregistrer le retour (ID manquant).";
	        }
	    } catch (Exception ex) {
	        message += "\nErreur lors de la mise à jour en base : " + ex.getMessage();
	    }

	    return message;
	}

	
	public void afficherEmpruntsEnCours() {
	    System.out.println("\n--- Liste des emprunts en cours ---");

	    boolean aucun = true;

	    for (Emprunt e : emprunts) {
	        if (e != null && e.getDateRetourReelle() == null) { // pas encore rendu
	            aucun = false;
	            e.afficherEmpruntEnCours(); // on reutilise la methode de Emprunt
	            System.out.println("-----------------------------");
	        }
	    }

	    if (aucun) {
	        System.out.println("Aucun emprunt en cours pour le moment ");
	    }
	}
	
	public void afficherDocuments() {
	    if (documents.isEmpty()) {
	        System.out.println("Aucun document dans le catalogue.");
	        return;
	    }
	    System.out.println("\nCatalogue :");
	    for (Document d : documents) {
	        String genre = (d.getGenre() == null || d.getGenre().isBlank()) ? "—" : d.getGenre();
	        System.out.println("• " + d.getTitreDocument() + " - " + d.getAuteur()
	                + " (" + genre + ") | Disponible : " + d.getStatut());
	    }
	}
	
	public void afficherAdherents() {
	    if (adherents.isEmpty()) {
	        System.out.println("Aucun adhérent enregistré.");
	        return;
	    }

	    System.out.println("\nListe des adhérents :");
	    for (Adherent a : adherents) {
	        String statut = a.getStatut() ? "Pénalité active" : "Aucun problème";
	        System.out.println("• " + a.getNom() + " " + a.getPrenom() +
	                " (" + a.getEmail() + ") | Statut : " + statut);
	    }
	}


	
	
	public void afficherAlertesRetards() {
		System.out.println("\n --- Liste des emprunts en retard ---");

	    boolean aucun = true;
	    LocalDate aujourdhui = LocalDate.now();

	    for (Emprunt e : emprunts) {
	        if (e != null 
	            && e.getDateRetourReelle() == null 
	            && e.getDateRetourPrevue().isBefore(aujourdhui)) {

	            aucun = false;
	            System.out.println("- '" + e.getDocument().getTitreDocument() +
	                "' emprunté par " + e.getAdherent().getNom() + " " + e.getAdherent().getPrenom() +
	                " (retard depuis le " + e.getDateRetourPrevue() + ")");
	        }
	    }

	    if (aucun) {
	        System.out.println("Aucun emprunt en retard");
	    }
	}
	
	
	public void afficherHistoriqueAdherent(Adherent a) {
	    if (a == null) {
	        System.out.println("Adhérent non valide !");
	        return;
	    }

	    List<Emprunt> historique = db.getHistoriqueAdherent(a.getEmail());
	    System.out.println("\n --- Historique des emprunts de " + a.getNom() + " " + a.getPrenom() + " ---");

	    if (historique.isEmpty()) {
	        System.out.println("Aucun emprunt trouvé pour cet adhérent");
	        return;
	    }

	    for (Emprunt e : historique) {
	        e.afficherHistorique();
	        System.out.println("------------------------------------");
	    }
	}

	
	public void afficherResumeAdherent(Adherent a) {
	    if (a == null) {
	        System.out.println("Adhérent non valide !");
	        return;
	    }
	    int total = 0;
	    int enCours = 0;
	    int enRetard = 0;
	    
	    LocalDate aujourdhui = LocalDate.now();
	    for (Emprunt e: emprunts) {
	        if (e != null && e.getAdherent() == a) {
	            total++;

	            if (e.getDateRetourReelle() == null) {
	                enCours++;

	                if (e.getDateRetourPrevue().isBefore(aujourdhui)) {
	                    enRetard++;
	                }
	            }
	        }
	    }

	    System.out.println("\n--- Résumé des emprunts de " + a.getNom() + " " + a.getPrenom() + " ---");
	    System.out.println("Total des emprunts : " + total);
	    System.out.println("Emprunts en cours : " + enCours);
	    System.out.println("Dont en retard : " + enRetard);
	    
	    // statut de l’adherent
	    if (a.getStatut()) {
	        System.out.println("Statut : PÉNALISÉ (emprunts suspendus)");
	    } else {
	        System.out.println("Statut : EN RÈGLE (peut emprunter)");
	    }
	}



	
	
}
