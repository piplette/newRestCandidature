package com.candidature.web;

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

import com.candidature.entities.Etat;

@Controller
@RequestMapping("/etat")
public class EtatController {
	
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

	/*****************************************/
	/***** RECHERCHER UN ETAT PAR ID *****/
	/*****************************************/
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findEtatById(@PathVariable("id") int etatId) {
		open();
		if(etatId <= 0){close();return new ResponseEntity<Object>("PAS ID", HttpStatus.BAD_REQUEST);}
		Etat etat = em.find(Etat.class, etatId);
		close();
		if(etat == null){return new ResponseEntity<Object>("ETAT ABSENT", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(etat, HttpStatus.OK);
	}

	/*****************************************/
	/***** RECHERCHER TOUS LES ETATS *****/
	/*****************************************/
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllEtat(@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select c from Etat c");
		List<Etat> etats = query.getResultList();
		close();
		if(etats.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(etats, HttpStatus.OK);
	}

	/****************************************/
	/******* ENREGISTREMENT D'UN ETAT *******/
	/****************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> createEtat(@RequestBody Etat etat) {
		open();
		if (etat.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);} 
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(etat);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		return new ResponseEntity<Object>("ETAT CREE", HttpStatus.CREATED);
	}
	
	/****************************************/
	/******** MISE A JOUR D'UN ETAT *********/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> updateEtat(@RequestBody Etat etat) {
		open();
		if (etat.getId() <= 0) { return new ResponseEntity<Object>("id vide", HttpStatus.BAD_REQUEST);}	
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Etat newEtat = em.find(Etat.class, etat.getId());
			if(newEtat == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}	
			newEtat.setNom(etat.getNom());
			em.flush();
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		} 
		close();
		return new ResponseEntity<Object>("MIS A JOUR", HttpStatus.OK);
	}
	
	/****************************************/
	/********* SUPPRESSION D'UN ETAT ********/
	/****************************************/
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteEtat(@PathVariable("id") int etatId) {
		open();
		if(etatId <= 0) { return new ResponseEntity<Object>("idEtat vide", HttpStatus.BAD_REQUEST);}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Etat etat = em.find(Etat.class, etatId);
			if(etat == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			em.remove(etat);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION", HttpStatus.BAD_REQUEST);
		} 
		close();
		return new ResponseEntity<Object>("SESSION SUPPRIME", HttpStatus.OK);
	}	
}
