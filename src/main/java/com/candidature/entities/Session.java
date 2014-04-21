package com.candidature.entities;

import java.io.Serializable;
import java.lang.String;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Entity implementation class for Entity: Session
 *
 */
@Entity
public class Session implements Serializable{
	   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SESSION_ID")
	private int id;
	
	@Column(nullable = false, unique = true)
	private String nom;
	
	@Column(name = "DATE_DEBUT", nullable = false)
	private Date dateDebut;
	
	@Column(name = "DATE_FIN", nullable = false)
	private Date dateFin;
	
	@Column(name = "PERIODE_SESSION", nullable = false)
	private Date periodeSession;
	
	@Transient
	private List<Candidature> candidatures;
	
	private static final long serialVersionUID = 1L;
	
	public Session() {
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
	
	public Date getDateDebut() {
		return this.dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}   
	public Date getDateFin() {
		return this.dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}   
	
	public Date getPeriodeSession() {
		return this.periodeSession;
	}

	public void setPeriodeSession(Date periodeSession) {
		this.periodeSession = periodeSession;
	}
	
	public List<Candidature> getListCandidatures() {
		return candidatures;
	}
	
	public void setListCandidatures(List<Candidature> candidatures) {
		this.candidatures = candidatures;
	}
}
