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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
	public static void sendMail(String destination, String contenu){  
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
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> findCandidatById(
			@PathVariable("id") int candidatId,
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		open();
		if(authorization == null) {
			close();
			return new ResponseEntity<Object>("COMPTE ABSENT", HttpStatus.UNAUTHORIZED);
		}else {
			Candidat candidat = null;
			// Recuperation currentUser
			try {
				candidat = Authorization.getCurrentUserByAuthorization(authorization);
				// Si erreur pendant la recuperation
			} catch (Exception e) {
				return new ResponseEntity<Object>("ERREUR", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if(candidat == null){return new ResponseEntity<Object>("MAUVAISE IDENTIFICATION", HttpStatus.UNAUTHORIZED);}
		}
		
		if(candidatId <= 0){close();return new ResponseEntity<Object>("PAS ID", HttpStatus.BAD_REQUEST);}
		Candidat candidat = em.find(Candidat.class, candidatId);
		close();
		if(candidat == null){return new ResponseEntity<Object>("CANDIDAT ABSENT", HttpStatus.NOT_FOUND);}
		return new ResponseEntity<Object>(candidat, HttpStatus.OK);
	}

	/*****************************************/
	/***** RECHERCHER TOUS LES CANDIDATS *****/
	/*****************************************/
	@RequestMapping(value = "/candidats", method = RequestMethod.GET, produces = "application/json")
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
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createCandidat(@RequestBody Candidat candidat) {
		open();
		if (candidat.getAdresse() == null) { return new ResponseEntity<Object>("adresse vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getCodePostal() == null) { return new ResponseEntity<Object>("cp vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getDiplome() == null) { return new ResponseEntity<Object>("diplome vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getEmail() == null) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getNom() == null) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPrenom() == null) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST); } 
		if (candidat.getSituationFamiliale() == null) { return new ResponseEntity<Object>("sitFam vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getTelephone() == null) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getVille() == null) { return new ResponseEntity<Object>("ville vide", HttpStatus.BAD_REQUEST);}
		Random r = new Random();
		int valeur = 100 + r.nextInt(900);
		String password = candidat.getNom()+ valeur;
		candidat.setPassword(password);
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
	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateCandidat(@RequestBody Candidat candidat) {
		open();
		if (candidat.getId() <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getAdresse() == null) { return new ResponseEntity<Object>("adresse vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getCodePostal() == null) { return new ResponseEntity<Object>("cp vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getDiplome() == null) { return new ResponseEntity<Object>("diplome vide", HttpStatus.BAD_REQUEST);} 
		if (candidat.getEmail() == null) { return new ResponseEntity<Object>("email vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getNom() == null) { return new ResponseEntity<Object>("nom vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPassword() == null) { return new ResponseEntity<Object>("password vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPrenom() == null) { return new ResponseEntity<Object>("prenom vide", HttpStatus.BAD_REQUEST); } 
		if (candidat.getSituationFamiliale() == null) { return new ResponseEntity<Object>("sitFam vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getTelephone() == null) { return new ResponseEntity<Object>("telephone vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getVille() == null) { return new ResponseEntity<Object>("ville vide", HttpStatus.BAD_REQUEST);}
		if (candidat.getPassword() == null) { return new ResponseEntity<Object>("password vide", HttpStatus.BAD_REQUEST);}
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
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Object> deleteSession(@PathVariable("id") int candidatId) {
		open();
		if(candidatId <= 0) { return new ResponseEntity<Object>("idCandidat vide", HttpStatus.BAD_REQUEST);}
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
