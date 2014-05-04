package com.candidature.auth;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.candidature.entities.Candidat;
import com.candidature.entities.Professeur;;

public class Authorization {
	// Renvoi l'utilisateur s'il existe
	// Retours possibles :
	// null : pas d'utilisateur
	// Annonceur : l'annonceur
	public static Professeur getCurrentProfesseurByAuthorization(String authorizationHeader) throws Exception {

		Professeur professeur = null;
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
							"SELECT a FROM Professeur a WHERE a.password = :password AND a.email = :email")
					.setParameter("password", password).setParameter("email", email);
			List<Professeur> professeurs = query.getResultList();
			if (!professeurs.isEmpty() && professeurs.size() == 1) {
				professeur = professeurs.get(0);
			}
			em.close();
			emf.close();
		}
		return professeur;
	}

	public static Candidat getCurrentCandidatByAuthorization(String authorizationHeader) throws Exception {

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