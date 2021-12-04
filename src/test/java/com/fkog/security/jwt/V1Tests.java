package com.fkog.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fkog.security.jwt.model.User;
import com.fkog.security.jwt.model.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class V1Tests {
	private static final String BASE_URL = "https://localhost:8443/v1";
	private static final UserDto STANDARD_USER = new UserDto();

	static {
		STANDARD_USER.setUsername("shubham");
		STANDARD_USER.setEmail("abc@fkog.com");
		STANDARD_USER.setBusinessTitle("developer");
		STANDARD_USER.setName("shubham");
		STANDARD_USER.setPhone("9899754628");
		STANDARD_USER.setPassword("mgidb@1234");
	}

	@Autowired
	private RestTemplate restTemplate;

	@Test
	public void testPublicHello() {
		ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/v1/public/hello",
				String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("[\"Public Hello!\"]");
		System.err.println(response.getBody());
	}

	@Test
	@DisplayName(("MUST be able to register new user"))
	public void t1() throws URISyntaxException {
		registerStandardUser();
	}

	private void registerStandardUser() throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		URI uri = new URI(BASE_URL + "/users/new");

		HttpEntity<UserDto> request = new HttpEntity<UserDto>(STANDARD_USER, headers);

		ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.POST, request, User.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		if (HttpStatus.OK == response.getStatusCode()) {
			User responseUser = response.getBody();
			System.err.println(responseUser);
			assertThat(responseUser.getUsername()).isEqualTo(STANDARD_USER.getUsername());
			assertThat(responseUser.getEmail()).isEqualTo(STANDARD_USER.getEmail());
			assertThat(responseUser.getRoles().size()).isEqualTo(1);
			assertThat(responseUser.getRoles().stream().anyMatch(r -> r.getName().equals("USER"))).isEqualTo(true);
		}

	}
}
