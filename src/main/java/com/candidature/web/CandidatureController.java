package com.candidature.web;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.candidature.entities.Candidature;
import com.candidature.entities.Etat;
import com.candidature.entities.Professeur;
import com.candidature.entities.Session;

@Controller
@RequestMapping("/candidature")
public class CandidatureController {
	
	private EntityManagerFactory emf;
	private EntityManager em;

	private void open() {
		emf = Persistence.createEntityManagerFactory("manager");
		em = emf.createEntityManager();
	}

	private void close() {
		em.close();
		emf.close();
	}

	/*********************************************/
	/***** RECHERCHER UNE CANDIDATURE PAR ID *****/
	/*********************************************/
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findCandidatureById(
			@PathVariable("id") int candidatureId) {
		if (candidatureId <= 0) {
			return new ResponseEntity<Object>("PAS ID", HttpStatus.BAD_REQUEST);
		}
		open();
		Candidature candidature = em.find(Candidature.class, candidatureId);
		close();
		if (candidature == null) {
			return new ResponseEntity<Object>("CANDIDATURE ABSENTE",
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(candidature, HttpStatus.OK);
	}

	/**********************************************/
	/***** RECHERCHER TOUTES LES CANDIDATURES *****/
	/**********************************************/
	@RequestMapping(value = "/getAllByCandidat/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllCandidatureByCandidat(
			@PathVariable("id") int candidatId,
			@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select c from Candidature c");
		List<Candidature> candidaturesTemp = query.getResultList();
		close();
		if(candidaturesTemp.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		Iterator<Candidature> it = candidaturesTemp.iterator();
		List<Candidature> candidatures = new ArrayList<Candidature>();
		while(it.hasNext()){
			Candidature candidature = it.next();
			if(candidature.getIdCandidat() == candidatId){
				candidatures.add(candidature);
			}
		}
		if(candidatures.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidatures, HttpStatus.OK);
	}

	/**********************************************/
	/***** RECHERCHER TOUTES LES CANDIDATURES *****/
	/**********************************************/
	@RequestMapping(value = "/getAllByProfesseur/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllCandidatureByProfesseur(
			@PathVariable("id") int professeurId,
			@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Professeur professeur = em.find(Professeur.class, professeurId);
		if(professeur == null){close();return new ResponseEntity<Object>("PROFESSEUR VIDE", HttpStatus.NOT_FOUND);}
		Query query = em.createQuery("select c from Candidature c");
		List<Candidature> candidaturesTemp = query.getResultList();
		close();
		if(candidaturesTemp.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		Iterator<Candidature> it = candidaturesTemp.iterator();
		List<Candidature> candidatures = new ArrayList<Candidature>();
		while(it.hasNext()){
			Candidature candidature = it.next();
			if(candidature.getIdSession() == professeur.getIdSession()){
				candidatures.add(candidature);
			}
		}
		if(candidatures.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidatures, HttpStatus.OK);
	}

	
	
	/********************************************/
	/***** ENREGISTREMENT D'UNE CANDIDATURE *****/
	/********************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createCandidature(
			@RequestBody Candidature candidature) {
		if (candidature.getMotivation().isEmpty()) {
			return new ResponseEntity<Object>("motivation vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getIdCandidat() <= 0) {
			return new ResponseEntity<Object>("idCandidat vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getIdSession() <= 0) {
			return new ResponseEntity<Object>("idSession vide",
					HttpStatus.BAD_REQUEST);
		}
		java.util.Date date = new java.util.Date();
		candidature.setDateInscription(new Date(date.getYear(),
				date.getMonth(), date.getDate()));
		candidature.setIdEtat(1);
		open();
		Etat etat = em.find(Etat.class, candidature.getIdEtat());
		if (etat == null) {
			close();
			return new ResponseEntity<Object>("ETAT NON TROUVE",
					HttpStatus.NOT_FOUND);
		}
		Session session = em.find(Session.class, candidature.getIdSession());
		if (session == null) {
			close();
			return new ResponseEntity<Object>("SESSION NON TROUVE",
					HttpStatus.BAD_REQUEST);
		}
		candidature.setNomEtat(etat.getNom());
		candidature.setNomSession(session.getNom());
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(candidature);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		return new ResponseEntity<Object>("CANDIDATURE CREE",
				HttpStatus.CREATED);
	}

	/****************************************/
	/***** MISE A JOUR D'UNE CANDIDATURE ****/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> updateCandidat(
			@RequestBody Candidature candidature) {
		if (candidature.getId() <= 0) {
			return new ResponseEntity<Object>("idCandidature vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getMotivation().isEmpty()) {
			return new ResponseEntity<Object>("motivation vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getIdCandidat() <= 0) {
			return new ResponseEntity<Object>("idCandidat vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getIdSession() <= 0) {
			return new ResponseEntity<Object>("idSession vide",
					HttpStatus.BAD_REQUEST);
		}
		if (candidature.getIdEtat() <= 0) {
			return new ResponseEntity<Object>("idEtat vide",
					HttpStatus.BAD_REQUEST);
		}
		open();
		Etat etat = em.find(Etat.class, candidature.getIdEtat());
		if (etat == null) {
			close();
			return new ResponseEntity<Object>("ETAT NON TROUVE",
					HttpStatus.NOT_FOUND);
		}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidature newCandidature = em.find(Candidature.class,
					candidature.getId());
			if (newCandidature == null) {
				return new ResponseEntity<Object>("CANDIDATURE ABSENTE",
						HttpStatus.NOT_FOUND);
			}
			newCandidature.setIdEtat(candidature.getIdEtat());
			newCandidature.setNomEtat(etat.getNom());
			em.flush();
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		return new ResponseEntity<Object>("CANDIDATURE MISE A JOUR",
				HttpStatus.OK);
	}

	/************************************************/
	/********* SUPPRESSION D'UNE CANDIDATURE ********/
	/************************************************/
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteCandidature(
			@PathVariable("id") int candidatureId) {
		if (candidatureId <= 0) {
			return new ResponseEntity<Object>("idCandidature vide",
					HttpStatus.BAD_REQUEST);
		}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidature candidature = em.find(Candidature.class, candidatureId);
			if (candidature == null) {
				return new ResponseEntity<Object>("NON TROUVE",
						HttpStatus.NOT_FOUND);
			}
			em.remove(candidature);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION",
					HttpStatus.BAD_REQUEST);
		}
		close();
		return new ResponseEntity<Object>("CANDIDATURE SUPPRIMEE",
				HttpStatus.OK);
	}

}
