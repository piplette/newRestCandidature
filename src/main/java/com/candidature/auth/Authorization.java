package com.candidature.auth;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.candidature.entities.Candidat;

public class Authorization {
	// Renvoi l'utilisateur s'il existe
	// Retours possibles :
	// null : pas d'utilisateur
	// Annonceur : l'annonceur
	public static Candidat getCurrentUserByAuthorizationById(String authorizationHeader) throws Exception {

		Candidat candidat = null;
		if (authorizationHeader != null) {
			String[] chaine = BasicAuth.decode(authorizationHeader);
			String email = chaine[0];
			String candidatId = chaine[1];
			System.out.println(email);
			System.out.println(candidatId);
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("manager");
			EntityManager em = emf.createEntityManager();
//			EntityTransaction tx = em.getTransaction();
//			tx.begin();
			Query query = em.createQuery(
							"SELECT a FROM Candidat a WHERE a.email = :email").setParameter("email", email);
//			@SuppressWarnings("unchecked")
			List<Candidat> candidats = query.getResultList();
			if (!candidats.isEmpty() && candidats.size() == 1) {
				candidat = candidats.get(0);
			}
			int id;
			try {
				id = Integer.parseInt(candidatId);
			}catch(NumberFormatException e){
				candidat = null;
				return candidat;
			}
			if(candidat.getId() != id){candidat = null;}
//			candidat = em.find(Candidat.class, candidatId);
//			if(candidat==null){System.out.println("c'est vide");}
//			if(candidat.getEmail().compareTo(email)!=0){candidat = null;}
			em.close();
			emf.close();
		}
		return candidat;
	}

	public static Candidat getCurrentUserByAuthorizationByPassword(String authorizationHeader) throws Exception {

		Candidat candidat = null;
		if (authorizationHeader != null) {
			String[] chaine = BasicAuth.decode(authorizationHeader);
			String email = chaine[0];
			String password = chaine[1];
			System.out.println(email);
			System.out.println(password);
			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("manager");
			EntityManager em = emf.createEntityManager();
			Query query = em
					.createQuery(
							"SELECT a FROM Candidat a WHERE a.password = :password AND a.email = :email")
					.setParameter("password", password).setParameter("email", email);
//			@SuppressWarnings("unchecked")
			List<Candidat> candidats = query.getResultList();
			if (!candidats.isEmpty() && candidats.size() == 1) {
				candidat = candidats.get(0);
			}
			em.close();
			emf.close();
		}
		return candidat;
	}
}