package com.sidekickjohn.demo;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.sidekickjohn.demo.boundary.SimpleRestEndpointController;
import com.sidekickjohn.demo.control.CallExternalService;
import com.sidekickjohn.demo.entity.Person;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles("test")
public class IntegrationTest {
	private TestRestTemplate testRestTemplate;
	public final String FANCY_URL = "https://my-fancy-url-doesnt-matter.com/person";
	private String apiUrl;
	private HttpHeaders headers;
	
	@LocalServerPort
	private String localServerPort;
	
	@MockBean
	RestTemplate restTemplate;
	
	@Autowired
	CallExternalService callExternalService;
	
	@Autowired
	SimpleRestEndpointController simpleRestEndpointController;
	
	@Before
	public void setup() {
		this.headers = new HttpHeaders();
		this.testRestTemplate = new TestRestTemplate("username", "password");
		this.apiUrl = String.format("http://localhost:%s/person", localServerPort);
	}
	
	@Test
	public void testShouldRetryOnceWhenTimelimitIsReached() {
		// Arrange
		Person mockPerson = new Person();
		mockPerson.setId(1);
		mockPerson.setFirstName("First");
		mockPerson.setLastName("Last");
		ResponseEntity<Person> mockResponse = new ResponseEntity<>(mockPerson, HttpStatus.OK);
			
		
		Answer customAnswer = new Answer() {
			private int invocationCount = 0;
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
				invocationCount++;
				if (invocationCount == 1) {
					Thread.sleep(6000);
					return new ResponseEntity<>(new Person(), HttpStatus.OK);
				} else {
					return mockResponse;
				}
			}
		};
		
		doAnswer(customAnswer)
		.when(restTemplate).exchange(
				FANCY_URL,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				new ParameterizedTypeReference<Person>() {});
		
		
		// Act
		ResponseEntity<Person> result = null;
		try {
			result = this.testRestTemplate.exchange(
					apiUrl,
					HttpMethod.GET,
					new HttpEntity<>(headers),
					new ParameterizedTypeReference<Person>() {
					});
		} catch(Exception ex) {
			System.out.println(ex);			
		}
		
		
		// Assert
		verify(restTemplate, times(2)).exchange(
				FANCY_URL,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				new ParameterizedTypeReference<Person>() {});
		Assert.assertNotNull(result);
		Assert.assertEquals(mockPerson, result.getBody());		
		
	}
	
	
}
