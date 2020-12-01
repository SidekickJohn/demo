package com.sidekickjohn.demo.control;


import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.sidekickjohn.demo.entity.Person;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Component
public class CallExternalService {
	public final String FANCY_URL = "https://my-fancy-url-doesnt-matter.com/person";
	private final static Logger LOGGER = LoggerFactory.getLogger(CallExternalService.class);
	private RestTemplate restTemplate;
	
	@Autowired
	public CallExternalService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@TimeLimiter(name = "MY_RESILIENCE_KEY")
	@Retry(name = "MY_RESILIENCE_KEY")
	public CompletableFuture<Person> getPersonById(int id) {
		Person person = new Person();
		HttpHeaders headers = new HttpHeaders();
		
		try {
			person = this.restTemplate.exchange(
					FANCY_URL,
					HttpMethod.GET,
					new HttpEntity<>(headers),
					new ParameterizedTypeReference<Person>() {
					}).getBody();
					
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error getting Person", ex);
		}
		
		return CompletableFuture.completedFuture(person);
	}
	
	

}
