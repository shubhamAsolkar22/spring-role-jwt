package com.fkog.security.jwt;

import com.fkog.security.jwt.apiError.ApiError;
import com.fkog.security.jwt.apiError.ApiErrorImpl;
import com.fkog.security.jwt.config.TokenProvider;
import com.fkog.security.jwt.dao.UserDao;
import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.LoginUser;
import com.fkog.security.jwt.model.LogoutUserDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fkog.security.jwt.model.User;
import com.fkog.security.jwt.model.UserDto;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class V1IntegrationTests {

	private static final String BASE_URL = "https://localhost:8443/v1";
	private static final UserDto STANDARD_USER = new UserDto();

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenProvider jwtTokenUtil;

	@Value("${jwt.header.string}")
	public String HEADER_STRING;

	@Value("${jwt.token.prefix}")
	public String TOKEN_PREFIX;

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
	@DisplayName(value = "MUST be able to register new user")
	public void t1() throws URISyntaxException {
		ResponseEntity<User> response = null;
		try {
			User u = userDao.findByUsername(STANDARD_USER.getUsername());
			if (u == null) {
				response = registerUser(STANDARD_USER);

				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

				if (HttpStatus.OK == response.getStatusCode()) {
					User responseUser = response.getBody();
					System.err.println(responseUser);
					assertThat(responseUser.getUsername()).isEqualTo(STANDARD_USER.getUsername());
					assertThat(responseUser.getEmail()).isEqualTo(STANDARD_USER.getEmail());
					assertThat(responseUser.getRoles().size()).isEqualTo(1);
					assertThat(responseUser.getRoles().stream().anyMatch(r -> r.getName().equals("USER")))
							.isEqualTo(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	@DisplayName(value = "registered user must be able to get jwt token using /v1/users/token")
	public void t2() throws URISyntaxException {
		getResponseOfRegisterUserQuery();

		ResponseEntity<AuthToken> response = getAuthTokenForStandardUser();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getToken()).isNotNull();
		assertThat(response.getBody().getRefreshToken()).isNotNull();
	}

	@Test
	@DisplayName(value = "registered user must be able to acquire refresh token using earlier non-expired jwt token")
	public void t3() throws URISyntaxException {

		getResponseOfRegisterUserQuery();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(STANDARD_USER.getUsername(), STANDARD_USER.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwtTokenUtil.generateToken(authentication);
		final String refreshToken = jwtTokenUtil.generateRefreshToken(authentication);

		AuthToken recentToken = new AuthToken(token, refreshToken);

		headers.add(HEADER_STRING, TOKEN_PREFIX + recentToken.getRefreshToken());

		HttpEntity request = new HttpEntity(headers);
		URI uri = new URI(BASE_URL + "/users//refreshed-token");

		ResponseEntity<AuthToken> response = restTemplate.exchange(uri, HttpMethod.GET, request, AuthToken.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getRefreshToken()).isEqualTo(recentToken.getRefreshToken());
		assertThat(response.getBody().getToken()).isNotNull();
	}

	@Test
//	@Disabled
	@DisplayName(value = "user with role = USER must not be able to access /v1/ping/adminping endpoint")
	public void t4() throws URISyntaxException {
		getResponseOfRegisterUserQuery();

		ResponseEntity<AuthToken> authTokenResponse = getAuthTokenForStandardUser();

		URI uriAdminPing = new URI(BASE_URL + "/ping/adminping");
		HttpHeaders adminPingHeader = new HttpHeaders();
		adminPingHeader.setContentType(MediaType.APPLICATION_JSON);
		adminPingHeader.add(HEADER_STRING, TOKEN_PREFIX + authTokenResponse.getBody().getRefreshToken());
		HttpEntity<?> requestAdminPing = new HttpEntity<>(adminPingHeader);
		boolean exceptionThrown = false;
		try {
			ResponseEntity<String> adminPingResponse = restTemplate.exchange(uriAdminPing, HttpMethod.GET,
					requestAdminPing, String.class);
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
			exceptionThrown = true;
		}

		assertThat(exceptionThrown).isTrue();
	}

	private ResponseEntity<AuthToken> getAuthTokenForStandardUser() throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		URI uri = new URI(BASE_URL + "/users/token");
		System.err.println("t2 uri " + uri.toString());
		LoginUser loginUser = new LoginUser();
		loginUser.setPassword(STANDARD_USER.getPassword());
		loginUser.setUsername(STANDARD_USER.getUsername());

		HttpEntity<LoginUser> requestAuthToken = new HttpEntity<LoginUser>(loginUser, headers);

		ResponseEntity<AuthToken> authTokenResponse = restTemplate.exchange(uri, HttpMethod.POST, requestAuthToken,
				AuthToken.class);
		return authTokenResponse;
	}

	@Test
	@DisplayName(value = "MUST return 422 error code if username is not acceptable")
	public void t5() throws URISyntaxException {

		ResponseEntity<ApiErrorImpl> response = null;
		UserDto invalidUser = new UserDto();
		invalidUser.setUsername("");
		invalidUser.setEmail("abc@fkog.com");
		invalidUser.setBusinessTitle("developer");
		invalidUser.setName("shubham");
		invalidUser.setPhone("+91 8779828648");
		invalidUser.setPassword("mgidb@1234");
		boolean exceptionThrown = false;
		try {
			User u = userDao.findByUsername(invalidUser.getUsername());
			if (u == null) {

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				URI uri = new URI(BASE_URL + "/users/new");

				HttpEntity<UserDto> request = new HttpEntity<UserDto>(invalidUser, headers);

				response = restTemplate.exchange(uri, HttpMethod.POST, request, ApiErrorImpl.class);

			}
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			System.out.println(e.getResponseBodyAsString());
			exceptionThrown = true;

		}
		assertThat(exceptionThrown).isTrue();
	}

	@Test
	@DisplayName(value = "/token endpoint must logout user when DELETE method is used")
	public void t6() throws URISyntaxException {
		getResponseOfRegisterUserQuery();

		ResponseEntity<AuthToken> authTokenResponse = getAuthTokenForStandardUser();

		URI logoutUri = new URI(BASE_URL + "/users/token");
		HttpHeaders logoutRequestHeader = new HttpHeaders();
		logoutRequestHeader.setContentType(MediaType.APPLICATION_JSON);
		logoutRequestHeader.add(HEADER_STRING, TOKEN_PREFIX + authTokenResponse.getBody().getRefreshToken());

		LogoutUserDto logoutUserDto = new LogoutUserDto();
		logoutUserDto.setUsername(STANDARD_USER.getUsername());
		Date beforeLogout = new Date();
//		assertThat(userDao.findByUsername(STANDARD_USER.getUsername()).getLastLoggedOut()).isNull();

		HttpEntity<?> requestLogout = new HttpEntity<>(logoutUserDto, logoutRequestHeader);
		ResponseEntity<String> logoutResponse = restTemplate.exchange(logoutUri, HttpMethod.DELETE, requestLogout,
				String.class);

		assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(userDao.findByUsername(STANDARD_USER.getUsername()).getLastLoggedOut()).isNotNull();
		assertThat(userDao.findByUsername(STANDARD_USER.getUsername()).getLastLoggedOut().after(beforeLogout)).isTrue();
	}

	private ResponseEntity<User> getResponseOfRegisterUserQuery() {
		ResponseEntity<User> response = null;
		try {
			User u = userDao.findByUsername(STANDARD_USER.getUsername());
			if (u == null) {
				response = registerUser(STANDARD_USER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private ResponseEntity<User> registerUser(UserDto userDto) throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		URI uri = new URI(BASE_URL + "/users/new");

		HttpEntity<UserDto> request = new HttpEntity<UserDto>(userDto, headers);

		return restTemplate.exchange(uri, HttpMethod.POST, request, User.class);

	}
}
