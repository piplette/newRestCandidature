package com.candidature.web;

import java.sql.Date;
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

import com.candidature.entities.Candidat;
import com.candidature.entities.Candidature;
import com.candidature.entities.Etat;

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
		if(candidatureId <= 0){return new ResponseEntity<Object>("PAS ID", HttpStatus.BAD_REQUEST);}
		open();
		Candidature candidature = em.find(Candidature.class, candidatureId);
		close();
		if(candidature == null){return new ResponseEntity<Object>("CANDIDATURE ABSENTE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidature, HttpStatus.OK);
	}

	/**********************************************/
	/***** RECHERCHER TOUTES LES CANDIDATURES *****/
	/**********************************************/
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllCandidature(@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select c from Candidature c");
		List<Candidature> candidatures = query.getResultList();
		close();
		if(candidatures.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidatures, HttpStatus.OK);
	}

	/********************************************/
	/***** ENREGISTREMENT D'UNE CANDIDATURE *****/
	/********************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createCandidature(@RequestBody Candidature candidature) {
		if (candidature.getMotivation() == null) { return new ResponseEntity<Object>("motivation vide", HttpStatus.BAD_REQUEST);} 
		if (candidature.getIdCandidat() <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);} 	
		if (candidature.getIdSession() <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		java.util.Date date = new java.util.Date();
		candidature.setDateInscription(new Date(date.getYear(), date.getMonth(), date.getDate()));
		candidature.setIdEtat(1);
		open();
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
		return new ResponseEntity<Object>("CANDIDATURE CREE", HttpStatus.CREATED);
	}
	
	/****************************************/
	/***** MISE A JOUR D'UNE CANDIDATURE ****/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> updateCandidat(@RequestBody Candidature candidature) {
		if (candidature.getId() <= 0) { return new ResponseEntity<Object>("idCandidature vide", HttpStatus.BAD_REQUEST);}
		if (candidature.getMotivation() == null) { return new ResponseEntity<Object>("motivation vide", HttpStatus.BAD_REQUEST);} 
		if (candidature.getIdCandidat() <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);} 	
		if (candidature.getIdSession() <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		if (candidature.getIdEtat() <= 0) { return new ResponseEntity<Object>("idEtat vide", HttpStatus.BAD_REQUEST);}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidature newCandidature = em.find(Candidature.class, candidature.getId());
			if( newCandidature == null){ return new ResponseEntity<Object>("CANDIDATURE ABSENTE", HttpStatus.NOT_FOUND);}
			newCandidature.setIdEtat(candidature.getIdEtat());
			em.flush();
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		} 
		close();
		return new ResponseEntity<Object>("CANDIDATURE MISE A JOUR", HttpStatus.OK);
	}
	
	/************************************************/
	/********* SUPPRESSION D'UNE CANDIDATURE ********/
	/************************************************/
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteCandidature(@PathVariable("id") int candidatureId) {
		if(candidatureId <= 0) { return new ResponseEntity<Object>("idCandidature vide", HttpStatus.BAD_REQUEST);}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidature candidature = em.find(Candidature.class, candidatureId);
			if(candidature == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			em.remove(candidature);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION", HttpStatus.BAD_REQUEST);
		} 
		close();
		return new ResponseEntity<Object>("CANDIDATURE SUPPRIMEE", HttpStatus.OK);
	}	
	
}
