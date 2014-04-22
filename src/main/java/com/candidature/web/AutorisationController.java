package com.candidature.web;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.candidature.entities.Candidat;
import com.candidature.entities.Candidature;

@Controller
@RequestMapping("/autorisation")
public class AutorisationController {
	
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

	/******************************************/
	/************** AUTORISATION **************/
	/******************************************/
	@RequestMapping(value = "/id={id}&email={email}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> getAutorisation(
			@PathVariable("id") int candidatId,
			@PathVariable("email") String email) {
		open();
		Candidat candidat = em.find(Candidat.class, candidatId);	
		if(candidat == null) {close();return new ResponseEntity<Object>("PAS DE CANDIDAT", HttpStatus.UNAUTHORIZED);}
		if(candidat.getEmail().compareTo(email)!=0){close();return new ResponseEntity<Object>("NON AUTORISE", HttpStatus.UNAUTHORIZED);}
		close();
		return new ResponseEntity<Object>("AUTORISE", HttpStatus.OK);
	}
}
