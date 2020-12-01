package com.sidekickjohn.demo.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidekickjohn.demo.control.CallExternalService;
import com.sidekickjohn.demo.entity.Person;

@RestController
@RequestMapping("/person")
public class SimpleRestEndpoint {
	private CallExternalService callExternalService;
	
	@Autowired
	public SimpleRestEndpoint(CallExternalService callExternalService) {
		this.callExternalService = callExternalService;		
	}
	
	
	@GetMapping("/{id}", produces = "application/json")
	public Person getPerson(@PathVariable int id) {
		return callExternalService.getPersonById(id).get();
	}

}
