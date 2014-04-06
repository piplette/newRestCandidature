package com.candidature.junit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.candidature.entities.Candidat;

public class CandidatTest {

    public static void main( String[] args )
    {
    	ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RestTemplate restTemplate = (RestTemplate)context.getBean("restTemplate");
		
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	String mot = "fdvsfds";
    	
    	Candidat candidat = new Candidat();
    	candidat.setAdresse("4, avenue gabriel peri");
    	candidat.setCodePostal("92350");
    	candidat.setDiplome(mot);
    	candidat.setEmail("kentish@hotmail.com");
    	candidat.setNom(mot);
    	candidat.setPassword(mot);
    	candidat.setPrenom("kentish");
    	candidat.setSituationFamiliale(mot);
    	candidat.setTelephone(mot);
    	candidat.setVille(mot);
    	
    	
		HttpEntity<Candidat> entity = new HttpEntity<Candidat>(candidat,headers);
		
		String url = "http://localhost:8080/RestCandidature/candidat";
		System.out.println("111111111111111111111111111");
//		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
//		ResponseEntity<Object> response2 = restTemplate.postForEntity(url, entity, Object.class);
//		System.out.println("iziiiiiiiiii");
//		System.out.println(response2.getBody());
//		restTemplate.getForObject(url, responseType, urlVariables)
//		ResponseEntity<Candidat> response = restTemplate.exchange(url, HttpMethod.GET, entity, Candidat.class);
//		Candidat candidat = response.getBody();
//		System.out.println(candidat.toString());
		//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
		ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);
		System.out.println("ya eu le retour");
		HttpStatus status = response.getStatusCode();
//		String restCall = response.getBody();
		System.out.println(status);
		
		
    }
}
