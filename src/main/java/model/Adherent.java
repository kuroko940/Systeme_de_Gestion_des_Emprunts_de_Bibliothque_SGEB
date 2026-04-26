package model;

public class Adherent {
	private Integer id;
	private String nom;
	private String prenom;
	private String email;
	private boolean penalite;
	
	public Adherent(String nom, String prenom, String email) {
		this.nom =nom;
		this.prenom=prenom;
		this.email=email;
		this.penalite = false; //par faut pas de penalite
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getPrenom() {
		return prenom;
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean getStatut() {
		return penalite;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public void setEmail(String mail) {
		this.email = mail;
	}
	
	public void setPenalite(boolean penalite) {
		this.penalite = penalite;
	}
	
	
	
	
	
	
}





