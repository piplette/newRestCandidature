package com.candidature.web;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
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
import com.candidature.entities.Candidat;

@Controller
@RequestMapping("/candidat")
public class CandidatController {

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
        final String username = "candidature.paris5@gmail.com";
        final String password = "candidature";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
         Session session = Session.getInstance(props,
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
			String contenuMessage = "Veuillez trouver votre mot de passe pour vous connecter à votre interface étudiante : "+contenu;
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
	public ResponseEntity<Object> loginCandidat(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		if(authorization == null) {
			return new ResponseEntity<Object>("AUTHENTIFICATION ABSENTE", HttpStatus.UNAUTHORIZED);
		}else {
			Candidat candidat = null;
			// Recuperation candidat
			try {
				candidat = Authorization.getCurrentCandidatByAuthorization(authorization);
				// Si erreur pendant la recuperation
			} catch (Exception e) {
				return new ResponseEntity<Object>("ERREUR", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(candidat == null){return new ResponseEntity<Object>("MAUVAISE IDENTIFICATION", HttpStatus.UNAUTHORIZED);}
			return new ResponseEntity<Object>(candidat, HttpStatus.OK);
		}	
		
		 
	}

	/*****************************************/
	/***** RECHERCHER UN CANDIDAT PAR ID *****/
	/*****************************************/
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findCandidatById(
			@PathVariable("id") int candidatId,
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		open();
		Candidat candidat = em.find(Candidat.class, candidatId);
		close();
		if(candidat == null){return new ResponseEntity<Object>("MAUVAISE AUTHENTIFICATION", HttpStatus.UNAUTHORIZED);}
		return new ResponseEntity<Object>(candidat, HttpStatus.OK);
	}

//	/*****************************************/
//	/***** RECHERCHER UN CANDIDAT PAR ID *****/
//	/*****************************************/
//	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
//	@ResponseBody
//	public ResponseEntity<Object> findCandidatById(
//			@PathVariable("id") int candidatId,
//			@RequestHeader(value = "Authorization", required = false) String authorization) {
//		if(authorization == null) {
//			return new ResponseEntity<Object>("AUTORISATION ABSENTE", HttpStatus.UNAUTHORIZED);
//		} else {
//			Candidat candidat = null;
//			try {
//				candidat = Authorization.getCurrentCandidatByAuthorization(authorization);
//			} catch (Exception e) {
//				return new ResponseEntity<Object>("ERREUR", HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			if(candidat == null){return new ResponseEntity<Object>("MAUVAISE AUTHENTIFICATION", HttpStatus.UNAUTHORIZED);}
//			return new ResponseEntity<Object>(candidat, HttpStatus.OK);
//		}
//	}
	
	/*****************************************/
	/***** RECHERCHER TOUS LES CANDIDATS *****/
	/*****************************************/
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findAllCandidat(@RequestParam(value = "sujet", required = false) String sujet) {
		open();
		Query query = em.createQuery("select c from Candidat c");
		List<Candidat> candidats = query.getResultList();
		close();
		if(candidats.size() == 0){return new ResponseEntity<Object>("TABLE VIDE", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidats, HttpStatus.OK);
	}

	/****************************************/
	/***** ENREGISTREMENT D'UN CANDIDAT *****/
	/****************************************/
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createCandidat(@RequestBody Candidat candidat) {
		if (candidat.getAdresse().isEmpty()) { return new ResponseEntity<Object>("adresse vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getCodePostal().isEmpty()) { return new ResponseEntity<Object>("cp vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getDiplome().isEmpty()) { return new ResponseEntity<Object>("diplome vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getEmail().isEmpty()) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPrenom().isEmpty()) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST); } 
		if (candidat.getSituationFamiliale().isEmpty()) { return new ResponseEntity<Object>("sitFam vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getTelephone().isEmpty()) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getVille().isEmpty()) { return new ResponseEntity<Object>("ville vide", HttpStatus.BAD_REQUEST);}
		Random r = new Random();
		int valeur = 100 + r.nextInt(900);
		String password = candidat.getNom()+ valeur;
		candidat.setPassword(password);
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			em.persist(candidat);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("Doublon", HttpStatus.CONFLICT);
		}
		close();
		sendMail(candidat.getEmail(), password);
		return new ResponseEntity<Object>("CANDIDAT CREE", HttpStatus.CREATED);
	}
	
	/****************************************/
	/***** ENREGISTREMENT D'UN CANDIDAT *****/
	/****************************************/
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateCandidat(@RequestBody Candidat candidat) {
		if (candidat.getId() <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getAdresse().isEmpty()) { return new ResponseEntity<Object>("adresse vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getCodePostal().isEmpty()) { return new ResponseEntity<Object>("cp vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getDiplome().isEmpty()) { return new ResponseEntity<Object>("diplome vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getEmail().isEmpty()) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getNom().isEmpty()) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPassword().isEmpty()) { return new ResponseEntity<Object>("password vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPrenom().isEmpty()) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST); } 
		if (candidat.getSituationFamiliale().isEmpty()) { return new ResponseEntity<Object>("sitFam vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getTelephone().isEmpty()) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getVille().isEmpty()) { return new ResponseEntity<Object>("ville vide", HttpStatus.BAD_REQUEST);}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidat newCandidat = em.find(Candidat.class, candidat.getId());
			if( newCandidat == null){ return new ResponseEntity<Object>("ID ABSENT", HttpStatus.NOT_FOUND);}
			newCandidat.setAdresse(candidat.getAdresse());
			newCandidat.setCodePostal(candidat.getCodePostal());
			newCandidat.setDiplome(candidat.getDiplome());
			newCandidat.setEmail(candidat.getEmail());
			newCandidat.setNom(candidat.getNom());
			newCandidat.setPassword(candidat.getPassword());
			newCandidat.setPrenom(candidat.getPrenom());
			newCandidat.setSituationFamiliale(candidat.getSituationFamiliale());
			newCandidat.setTelephone(candidat.getTelephone());
			newCandidat.setVille(candidat.getVille());
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
	public ResponseEntity<Object> deleteCandidat(@PathVariable("id") int candidatId) {
		if(candidatId <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);}
		open();
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Candidat candidat = em.find(Candidat.class, candidatId);
			if(candidat == null){ return new ResponseEntity<Object>("NON TROUVE", HttpStatus.NOT_FOUND);}
			em.remove(candidat);
			tx.commit();
		} catch (PersistenceException te) {
			close();
			return new ResponseEntity<Object>("ERREUR SUPPRESSION", HttpStatus.BAD_REQUEST);
		} 
		close();
		return new ResponseEntity<Object>("CANDIDAT SUPPRIME", HttpStatus.OK);
	}	
}
