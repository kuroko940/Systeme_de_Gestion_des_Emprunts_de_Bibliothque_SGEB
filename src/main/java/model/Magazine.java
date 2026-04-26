package model;

public class Magazine extends Document{
	private int numero;
    private int periodicite;
    
    public Magazine(String titre, String auteur) {
        super(titre, auteur); 
    }
    
    public Magazine(String titre, String auteur, int numero, int periodicite) {
        super(titre, auteur);  
        this.numero = numero;
        this.periodicite = periodicite;
    }

    public int getNumero() { 
    	return numero;
    }
    public void setNumero(int numero) { 
    	this.numero = numero; 
    }
    
    
    public int getPeriodicite() { 
    	return periodicite; 
    }
    public void setPeriodicite(int periodicite) { 
    	this.periodicite = periodicite; 
    }
	
}
