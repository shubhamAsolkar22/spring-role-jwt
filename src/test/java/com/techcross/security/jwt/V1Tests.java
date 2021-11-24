package com.techcross.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.techcross.security.jwt.model.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class V1Tests {
//	@Autowired
//	WebClient webClient;
	
	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void registersUser() throws JsonMappingException, JsonProcessingException {
		UserDto udto = new UserDto();
		udto.setBusinessTitle("developer");
		udto.setEmail("abc@sd.com");
		udto.setName("shubham");
		udto.setPassword("password");
		udto.setUsername("shubham");
		
		webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(udto)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}

}
