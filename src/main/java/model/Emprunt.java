package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {
	private Integer id;
	private Document document;
    private Adherent adherent;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle; // null si pas rendu
   

    
    public Emprunt(Document document, Adherent adherent, LocalDate dateEmprunt) {
        this.document = document;
        this.adherent = adherent;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateEmprunt.plusWeeks(3);
        this.dateRetourReelle = null;
    }
    
    
    public boolean estDisponible() {
    	return document.getStatut();
    }
    
    public Document getDocument() {
    	return document;
    }
    
    public boolean getStatutAdherent() {
    	return adherent.getStatut();
    }
    
    public Adherent getAdherent() {
    	return adherent;
    }
    
    public LocalDate getDateEmprunt() {
    	return dateEmprunt;
    }
    
    public LocalDate getDateRetourReelle() {
    	return dateRetourReelle;
    }
    
    public LocalDate getDateRetourPrevue() {
    	return dateRetourPrevue;
    }
    
    public void setDateRetourPrevue(LocalDate l) {
    	this.dateRetourPrevue = l;
    }
    
    public void setDateRetourReelle(LocalDate l) {
    	this.dateRetourReelle = l;
    }
    
    public double retour(LocalDate dateRetour) {
        this.dateRetourReelle = dateRetour;
        long joursDeRetard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourReelle);
        document.setStatutDocument(true);
        if (joursDeRetard > 0) {
            return joursDeRetard * 0.5;
        } else {
            return 0.0;
        }
    }
    
    public void afficherEmpruntEnCours() {
    	 System.out.print("Emprunt du document : ");
    	 document.afficherDocument();
         System.out.println("Par : " + adherent.getNom() +" "+ adherent.getPrenom());
         System.out.println("Date de retour prévue : " + dateRetourPrevue);
    }
    
    public void afficherHistorique() {
        System.out.println("Document : " + document.getTitreDocument() + " (" + document.getAuteur() + ")");
        System.out.println("Date d'emprunt : " + dateEmprunt);
        System.out.println("Date de retour prévue : " + dateRetourPrevue);

        if (dateRetourReelle == null) {
            System.out.println("Statut : En cours");
        } else {
            System.out.println("Rendu le : " + dateRetourReelle);
        }
    }

    
  
}
