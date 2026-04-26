package model;

public class Livre extends Document {
	private String isbn;
	private Integer nbPages;
	
	public Livre(String titre, String auteur) {
        super(titre, auteur);
    }
	
	public Livre(String titre, String auteur,String isbn, Integer nbPages) {
        super(titre, auteur);
        this.isbn = isbn;
        this.nbPages = nbPages;
    }
	
	public String getIsbn() { 
		return isbn; 
	}
	public void setIsbn(String isbn) { 
		this.isbn = isbn; 
	}
	

    public Integer getNbPages() { 
    	return nbPages; 
    }
    public void setNbPages(Integer nbPages) { 
    	this.nbPages = nbPages; 
    }

}
	
	

