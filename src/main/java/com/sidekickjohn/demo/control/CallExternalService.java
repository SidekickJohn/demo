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
import com.sidekickjohn.demo.entity.Post;

import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Component
public class CallExternalService implements ICallExternalService {
	public final String FANCY_URL = "https://jsonplaceholder.typicode.com/posts/1";
	private final static Logger LOGGER = LoggerFactory.getLogger(CallExternalService.class);
	private RestTemplate restTemplate;
	
	@Autowired
	public CallExternalService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@TimeLimiter(name = "MY_RESILIENCE_KEY")
	@Retry(name = "MY_RESILIENCE_KEY")
	public CompletableFuture<Person> getPersonById() {
		Person person = new Person();
		Post post = new Post();
		HttpHeaders headers = new HttpHeaders();
		
		try {
			post = this.restTemplate.exchange(
					FANCY_URL,
					HttpMethod.GET,
					new HttpEntity<>(headers),
					new ParameterizedTypeReference<Post>() {
					}).getBody();
					
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error getting Post", ex);
		}
		
		person = mapPostToPerson(post);
		
		return CompletableFuture.completedFuture(person);
	}
	
	
	private Person mapPostToPerson(Post post) {
		Person newPerson = new Person();
		if (null != post) {
			newPerson.setId(post.getId());
			newPerson.setFirstName(post.getTitle());
			newPerson.setLastName(post.getBody());
		} else {
			LOGGER.error("post is null");
		}
				
		return newPerson;		
	}
	

}
