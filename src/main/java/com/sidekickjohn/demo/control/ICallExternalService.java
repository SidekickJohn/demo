package com.sidekickjohn.demo.control;

import java.util.concurrent.CompletableFuture;

import com.sidekickjohn.demo.entity.Person;

public interface ICallExternalService {
	public CompletableFuture<Person> getPersonById();
}
