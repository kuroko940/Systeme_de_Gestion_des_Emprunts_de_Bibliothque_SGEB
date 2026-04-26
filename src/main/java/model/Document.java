package model;

public abstract class Document {
	private Integer id;
    private String titre;
    private String auteur;
    private String genre;
    private boolean disponible = true;

    public Document(String titre, String auteur) {
        this.titre = titre;
        this.auteur = auteur;
    }
    
    public Integer getId() {
    	return id;
    }
    
    public boolean getStatut() {
    	return disponible;
    }
    
    public String getGenre() {
    	return genre;
    }
    
    public String getTitreDocument() {
    	return titre;
    }
    
    public String getAuteur() {
    	return auteur;
    }
    
    
    public void setStatutDocument(boolean disponible) {
    	this.disponible = disponible;
    }
    
    public void setId(Integer id) {
    	this.id = id;
    }
    
    
    public void setGenre(String genre) {
    	 this.genre = genre;
    }
    
    public void setTitreDocument(String titre) {
    	this.titre = titre;;
    }
    
    public void setAuteur(String auteur) {
    	this.auteur = auteur;
    }
    
    public void afficherDocument(){
    	System.out.println("'"+ titre +"' de "+auteur+"");
    }
    
    
}
