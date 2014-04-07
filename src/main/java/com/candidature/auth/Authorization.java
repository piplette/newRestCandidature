package com.candidature.auth;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.candidature.entities.Candidat;

public class Authorization {
	// Renvoi l'utilisateur s'il existe
	// Retours possibles :
	// null : pas d'utilisateur
	// Annonceur : l'annonceur
	public static Candidat getCurrentUserByAuthorization(String authorizationHeader) throws Exception {

		Candidat candidat = null;
		if (authorizationHeader != null) {
			String[] chaine = BasicAuth.decode(authorizationHeader);
			String email = chaine[0];
			String password = chaine[1];
			System.out.println(email);
			System.out.println(password);
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("manager");
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			Query query = em.createQuery(
							"SELECT a FROM Candidat a WHERE a.password = :password AND a.email = :email")
					.setParameter("password", password).setParameter("email", email);
//			@SuppressWarnings("unchecked")
			List<Candidat> candidats = query.getResultList();
			if (!candidats.isEmpty() && candidats.size() == 1) {
				candidat = candidats.get(0);
			}
		}
		return candidat;
	}

//	public static Candidat getCurrentSimpleUserByAuthorization(String authorizationHeader) throws Exception {
//
//		Candidat candidat = null;
//		if (authorizationHeader != null) {
//			String[] chaine = BasicAuth.decode(authorizationHeader);
//			String login = chaine[0];
//			String mdp = chaine[1];
//			EntityManagerFactory emf = Persistence
//					.createEntityManagerFactory("manager");
//			EntityManager em = emf.createEntityManager();
//			EntityTransaction tx = em.getTransaction();
//			tx.begin();
//			Query query = em
//					.createQuery(
//							"SELECT a FROM Candidat a WHERE a.password = :mdp AND a.email = :login")
//					.setParameter("paswword", mdp).setParameter("email", login);
//			@SuppressWarnings("unchecked")
//			List<Candidat> candidats = query.getResultList();
//			if (!candidats.isEmpty() && candidats.size() == 1) {
//				Candidat candidatComplet = candidats.get(0);
//				AnnonceurSimple currentUser = new AnnonceurSimple();
//				currentUser.setIdAnnonceur(annonceurComplet.getIdAnnonceur());
//				response = currentUser;
//			}
//		}
//		return response;
//
//	}

}