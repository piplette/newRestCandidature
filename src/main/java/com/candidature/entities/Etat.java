package com.candidature.entities;

import java.io.Serializable;
import java.lang.String;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Etat
 *
 */
@Entity
public class Etat implements Serializable{
	   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ETAT_ID")
	private int id;
	
	@Column(nullable = false, unique = true)
	private String nom;
	
	@Transient
	private List<Candidature> candidatures;
	
	private static final long serialVersionUID = 1L;
	
	public Etat() {
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
	
	public List<Candidature> getListCandidatures() {
		return candidatures;
	}
	
	public void setListCandidatures(List<Candidature> candidatures) {
		this.candidatures = candidatures;
	}
}
