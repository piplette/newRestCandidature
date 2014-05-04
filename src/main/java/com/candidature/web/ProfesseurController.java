package com.candidature.web;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.candidature.auth.Authorization;
import com.candidature.entities.Professeur;
import com.candidature.entities.Session;

@Controller
@RequestMapping("/professeur")
public class ProfesseurController {
	
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
	/**** ENVOI DU MOT DE PASSE PAR EMAIL ****/
	/*****************************************/
	private void sendMail(String destination, String contenu){  
        final String username = "professeurure.paris5@gmail.com";
        final String password = "professeurure";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        javax.mail.Session session = javax.mail.Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
        try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
			InternetAddress.parse(destination));
			String sujet = "Inscription à l'université ";
			String contenuMessage = "Veuillez trouver votre mot de passe pour vous connecter à votre interface professeur : "+contenu;
			message.setSubject(sujet);
			message.setContent(contenuMessage, "text/html;charset=UTF-8");
			Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
	
	/*****************************************/
	/***** RECHERCHER UN CANDIDAT PAR ID *****/
	/*****************************************/
	@RequestMapping(value = "/login", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> loginProfesseur(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		if(authorization == null) {
			return new ResponseEntity<Object>("AUTHENTIFICATION ABSENTE", HttpStatus.UNAUTHORIZED);
		}else {
			Professeur professeur = null;
			// Recuperation professeur
			try {
				professeur = Authorization.getCurrentProfesseurByAuthorization(authorization);
				// Si erreur pendant la recuperation
			} catch (Exception e) {
				return new ResponseEntity<Object>("ERREUR", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(professeur == null){return new ResponseEntity<Object>("MAUVAISE IDENTIFICATION", HttpStatus.UNAUTHORIZED);}
			return new ResponseEntity<Object>(professeur, HttpStatus.OK);
		}	
		
		 
	}

	/*****************************************/
	/***** RECHERCHER UN CANDIDAT PAR ID *****/
	/*****************************************/
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findProfesseurById(
			@PathVariable("id") int professeurId,
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		if(authorization == null) {
			return new ResponseEntity<Object>("AUTORISATION ABSENTE", HttpStatus.UNAUTHORIZED);
		} else {
			Professeur professeur = null;
			try {
				professeur = Authorization.getCurrentProfesseurByAuthorization(authorization);
			} catch (Exception e) {
				return new ResponseEntity<Object>("ERREUR", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(professeur == null){return new ResponseEntity<Object>("MAUVAISE AUTHENTIFICATION", HttpStatus.UNAUTHORIZED);}
			return new ResponseEntity<Object>(professeur, HttpStatus.OK);
		}
	}

	/*****************************************/
	/***** RECHERCHER TOUS LES CANDIDATS *****/
	/*****************************************/
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllProfesseur(@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select c from Professeur c");
		List<Professeur> professeurs = query.getResultList();
		close();
		if(professeurs.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(professeurs, HttpStatus.OK);
	}

	/****************************************/
	/***** ENREGISTREMENT D'UN CANDIDAT *****/
	/****************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createProfesseur(@RequestBody Professeur professeur) { 	
		if (professeur.getEmail().isEmpty()) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getIdSession() <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getPrenom().isEmpty()) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST);} 
		if (professeur.getTelephone().isEmpty()) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		Random r = new Random();
		int valeur = 100 + r.nextInt(900);
		String password = professeur.getNom()+ valeur;
		professeur.setPassword(password);
		open();
		Session session = em.find(Session.class, professeur.getIdSession());
		if(session == null){close();return new ResponseEntity<Object>("SESSION NON TROUVE", HttpStatus.NOT_FOUND);}
		professeur.setNomSession(session.getNom());
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(professeur);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		sendMail(professeur.getEmail(), password);
		return new ResponseEntity<Object>("CANDIDAT CREE", HttpStatus.CREATED);
	}
	
	/****************************************/
	/***** ENREGISTREMENT D'UN CANDIDAT *****/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateProfesseur(@RequestBody Professeur professeur) {
		if (professeur.getId() <= 0) { return new ResponseEntity<Object>("idProfesseur vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getEmail().isEmpty()) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getIdSession() <= 0) { return new ResponseEntity<Object>("idSession vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (professeur.getPrenom().isEmpty()) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST);} 
		if (professeur.getTelephone().isEmpty()) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		open();
		Session session = em.find(Session.class, professeur.getIdSession());
		if(session == null){close();return new ResponseEntity<Object>("SESSION NON TROUVE", HttpStatus.NOT_FOUND);}
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Professeur newProfesseur = em.find(Professeur.class, professeur.getId());
			if(newProfesseur == null){close();return new ResponseEntity<Object>("PROFESSEUR ABSENT", HttpStatus.NOT_FOUND);}
			newProfesseur.setEmail(professeur.getEmail());
			newProfesseur.setNom(professeur.getNom());
			newProfesseur.setPassword(professeur.getPassword());
			newProfesseur.setPrenom(professeur.getPrenom());
			newProfesseur.setTelephone(professeur.getTelephone());
			newProfesseur.setNomSession(session.getNom());
			em.flush();
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		} 
		close();
		return new ResponseEntity<Object>("OK", HttpStatus.OK);
	}
	
	/****************************************/
	/******* SUPPRESSION D'UN CANDIDAT ******/
	/****************************************/
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteProfesseur(@PathVariable("id") int professeurId) {
		if(professeurId <= 0) { return new ResponseEntity<Object>("idProfesseur vide", HttpStatus.BAD_REQUEST);}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Professeur professeur = em.find(Professeur.class, professeurId);
			if(professeur == null){close();return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			em.remove(professeur);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION", HttpStatus.BAD_REQUEST);
		} 
		close();
		return new ResponseEntity<Object>("CANDIDAT SUPPRIME", HttpStatus.OK);
	}	
}
