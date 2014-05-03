package com.candidature.web;

import java.util.ArrayList;
import java.util.Date;
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

import com.candidature.entities.Session;

@Controller
@RequestMapping("/session")
public class SessionController {
	
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
	/***** RECHERCHER UNE SESSION PAR ID *****/
	/*****************************************/
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findSessionById(@PathVariable("id") int sessionId) {
		open();
		if(sessionId <= 0){close();return new ResponseEntity<Object>("PAS ID", HttpStatus.BAD_REQUEST);}
		Session session = em.find(Session.class, sessionId);
		close();
		if(session== null){return new ResponseEntity<Object>("SESSION ABSENTE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(session, HttpStatus.OK);
	}

	/*****************************************/
	/***** RECHERCHER TOUS LES SESSIONS ******/
	/*****************************************/
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllSessions(@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select s from Session s");
		List<Session> allSessions = query.getResultList();
		close();
		if(allSessions.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		Iterator<Session> it = allSessions.iterator();
		Date dateNow = new Date();
		ArrayList<Session> sessions = new ArrayList<Session>();
		while(it.hasNext()){
			Session session = it.next();
			System.out.println(session.getId());
			System.out.println("date objet : "+session.getPeriodeSession());
			System.out.println("date now : "+dateNow);
			if(session.getPeriodeSession().compareTo(dateNow) == 0){System.out.println("date Žgale");}
			if(session.getPeriodeSession().compareTo(dateNow) < 0){System.out.println("date dŽpassŽe");}
			if(session.getPeriodeSession().compareTo(dateNow) > 0){System.out.println("date valide");sessions.add(session);}
		}
		return new ResponseEntity<Object>(sessions, HttpStatus.OK);
	}

	/****************************************/
	/***** ENREGISTREMENT D'UNE SESSION *****/
	/****************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createSession(@RequestBody Session session) {
		open();
		if (session.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);} 
		if (session.getDateDebut() == null) { return new ResponseEntity<Object>("dateDebut vide", HttpStatus.BAD_REQUEST);} 
		if (session.getDateFin() == null) { return new ResponseEntity<Object>("dateFin vide", HttpStatus.BAD_REQUEST);} 
		if (session.getPeriodeSession() == null) { return new ResponseEntity<Object>("periodeSession vide", HttpStatus.BAD_REQUEST);}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(session);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		return new ResponseEntity<Object>("SESSION CREE", HttpStatus.CREATED);
	}
	
	/****************************************/
	/******* MISE A JOUR D'UNE SESSION ******/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateSession(@RequestBody Session session) {
		open();
		if(session.getId() <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		if (session.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);} 
		if (session.getDateDebut() == null) { return new ResponseEntity<Object>("dateDebut vide", HttpStatus.BAD_REQUEST);} 
		if (session.getDateFin() == null) { return new ResponseEntity<Object>("dateFin vide", HttpStatus.BAD_REQUEST);} 
		if (session.getPeriodeSession() == null) { return new ResponseEntity<Object>("periodeSession vide", HttpStatus.BAD_REQUEST);}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Session newSession = em.find(Session.class, session.getId());
			if(newSession == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			newSession.setNom(session.getNom());
			newSession.setDateDebut(session.getDateDebut());
			newSession.setDateFin(session.getDateFin());
			newSession.setPeriodeSession(session.getPeriodeSession());
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
	/******* SUPPRESSION D'UNE SESSION ******/
	/****************************************/
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteSession(@PathVariable("id") int sessionId) {
		open();
		if(sessionId <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Session session = em.find(Session.class, sessionId);
			if(session == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			em.remove(session);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION", HttpStatus.BAD_REQUEST);
		} 
		close();
		return new ResponseEntity<Object>("SESSION SUPPRIME", HttpStatus.OK);
	}
}
