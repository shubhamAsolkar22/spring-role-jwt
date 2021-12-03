package com.techcross.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class V1Tests {
	@Autowired
	WebClient webClient;

//	@Autowired
//	private WebTestClient webTestClient;

	@Autowired
	private RestTemplate restTemplate;

//	@Test
//	public void registersUser() throws JsonMappingException, JsonProcessingException {
//		UserDto udto = new UserDto();
//		udto.setBusinessTitle("developer");
//		udto.setEmail("abc@sd.com");
//		udto.setName("shubham");
//		udto.setPassword("password");
//		udto.setUsername("shubham");
//		
//		webTestClient.post().uri("/users").contentType(MediaType.APPLICATION_JSON)
//				.accept(MediaType.APPLICATION_JSON)
//				.bodyValue(udto)
//				.exchange()
//				.expectStatus().isOk()
//				.expectHeader().contentType(MediaType.APPLICATION_JSON)
//				.expectBody(); 
//	}

	@Test
	@Disabled
	@DisplayName(("Test v1/public/hello using https configured restTemplate"))
	public void testPublicHello() {
		ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/v1/public/hello",
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("[\"Public Hello!\"]");
		System.err.println(response.getBody());
	}

	@Test
	@DisplayName(("Test v1/public/hello using https configured web client"))
	public void testPublicHelloWebClient() {
		webClient.post().uri("/v1/public/hello")
		.retrieve()
		.bodyToMono(String.class)
		.subscribe(s -> assertThat(s).isEqualTo("[\"Public Hello!\"]"));
	}
}
