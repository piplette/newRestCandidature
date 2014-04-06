package com.candidature.entities;

import java.io.Serializable;
import java.lang.String;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Entity implementation class for Entity: Candidat
 *
 */
@Entity
public class Candidat implements Serializable{
	   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CANDIDAT_ID")
	private int id;
	
	@Column(nullable = false)
	private String nom;
	
	@Column(nullable = false)
	private String prenom;
	
	@Column(nullable = false, unique = true)
	private String telephone;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String diplome;
	
	@Column(name = "SITUATION_FAMILIALE", nullable = false)
	private String situationFamiliale;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String adresse;
	
	@Column(name = "CODE_POSTAL", length = 5, nullable = false)
	private String codePostal;
	
	@Column(nullable = false)
	private String ville;
	
	private static final long serialVersionUID = 1L;
	
	@OneToOne(mappedBy="candidat") 
	private Candidature candidature;

	public Candidat() {
		super();
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	
	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}  
	
	public String getPrenom() {
		return this.prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}   
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}   
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDiplome() {
		return this.diplome;
	}

	public void setDiplome(String diplome) {
		this.diplome = diplome;
	}
	
	public String getSituationFamiliale() {
		return this.situationFamiliale;
	}

	public void setSituationFamiliale(String situationFamiliale) {
		this.situationFamiliale = situationFamiliale;
	}
	
	@JsonIgnore
	public Candidature getCandidature() {
		return candidature;
	}
	
	public void setCandidature(Candidature candidature) {
		this.candidature = candidature;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAdresse() {
		return this.adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	
	public String getCodePostal() {
		return this.codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}	
	
	public String getVille() {
		return this.ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}
	
	public String toString() {
		return "Candidat [id=" + id + ", nom=" + nom + ", prenom=" + prenom
				+ ", telephone=" + telephone + ", email=" + email
				+ ", diplome=" + diplome + ", situationFamiliale=" + situationFamiliale
				+ ", password=" + password 
				+ ", adresse=" + adresse + ", codePostal=" + codePostal
				+ ", ville=" + ville + ", candidature=" + candidature + "]";
	}
}
