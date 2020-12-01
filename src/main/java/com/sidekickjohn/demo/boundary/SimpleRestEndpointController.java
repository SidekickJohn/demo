package com.sidekickjohn.demo.boundary;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidekickjohn.demo.control.CallExternalService;
import com.sidekickjohn.demo.entity.Person;

@RestController
public class SimpleRestEndpointController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRestEndpointController.class);
	private CallExternalService callExternalService;
	
	@Autowired
	public SimpleRestEndpointController(CallExternalService callExternalService) {
		this.callExternalService = callExternalService;		
	}
	
	
	@GetMapping("/person")
	public Person getPerson() {
		try {
			return callExternalService.getPersonById().get();
		} catch(InterruptedException | ExecutionException ex) {
			LOGGER.error("failed during get", ex);
			throw new RuntimeException("FATAL");
		}
	}

}
