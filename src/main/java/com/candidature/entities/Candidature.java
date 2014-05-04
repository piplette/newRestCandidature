package com.candidature.entities;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.*;

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
	
	@Column(name = "CANDIDAT_ID", nullable = false)
	private int idCandidat;
	
	@Column(name = "SESSION_ID", nullable = false)
	private int idSession;
	
	@Column(name = "SESSION_NOM", nullable = false)
	private String nomSession;
	
	@Column(name = "ETAT_ID", nullable = false)
	private int idEtat;
	
	@Column(name = "ETAT_NOM", nullable = false)
	private String nomEtat;

	private static final long serialVersionUID = 1L;
	
	
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
	
	public String getMotivation() {
		return this.motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	public int getIdCandidat() {
		return idCandidat;
	}

	public void setIdCandidat(int idCandidat) {
		this.idCandidat = idCandidat;
	}

	public int getIdSession() {
		return idSession;
	}

	public void setIdSession(int idSession) {
		this.idSession = idSession;
	}
	
	public String getNomSession() {
		return nomSession;
	}

	public void setNomSession(String nomSession) {
		this.nomSession = nomSession;
	}

	public int getIdEtat() {
		return idEtat;
	}

	public void setIdEtat(int idEtat) {
		this.idEtat = idEtat;
	}
	
	public String getNomEtat() {
		return nomEtat;
	}

	public void setNomEtat(String nomEtat) {
		this.nomEtat = nomEtat;
	}
}
