package com.candidature.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;


/**
 * Entity implementation class for Entity: Candidature
 *
 */
@Entity
public class Candidature implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CANDIDATURE_ID")
	private int id;
	
	@Column(name = "DATE_INSCRIPTION", nullable = false)
	private Date dateInscription;
	
	@Column(nullable = false)
	private String motivation;
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne	
	private Session session;
	
	@ManyToOne
	private Etat etat;
	
	@OneToOne
	private Candidat candidat;
	
	public Candidature() {
		super();
	}  
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Date getDateInscription() {
		return this.dateInscription;
	}

	public void setDateInscription(Date dateInscription) {
		this.dateInscription = dateInscription;
	}	

	@JsonIgnore
	public Session getSession() {
		return session;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	@JsonIgnore
	public Etat getEtat() {
		return etat;
	}
	
	public void setEtat(Etat etat) {
		this.etat = etat;
	}
	
	@JsonIgnore
	public Candidat getCandidat() {
		return candidat;
	}
	
	public Candidat getCandidatCandidature() {
		return candidat;
	}
	
	public void setCandidat(Candidat candidat) {
		this.candidat = candidat;
	}
	
	public String getMotivation() {
		return this.motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}
}
