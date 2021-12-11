package com.fkog.security.jwt;

import com.fkog.security.jwt.config.TokenProvider;
import com.fkog.security.jwt.dao.UserDao;
import com.fkog.security.jwt.model.AuthToken;
import com.fkog.security.jwt.model.LoginUser;
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
                response = registerStandardUser(STANDARD_USER);

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName(value = "registered user must be able to get jwt token using /v1/users/token")
    public void t2() throws URISyntaxException {
        getResponseOfRegisterUserQuery();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI(BASE_URL + "/users/token");
        System.err.println("t2 uri " + uri.toString());
        LoginUser loginUser = new LoginUser();
        loginUser.setPassword(STANDARD_USER.getPassword());
        loginUser.setUsername(STANDARD_USER.getUsername());

        HttpEntity<LoginUser> request = new HttpEntity<LoginUser>(loginUser, headers);

        ResponseEntity<AuthToken> response = restTemplate.exchange(uri, HttpMethod.POST, request, AuthToken.class);
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
        
        headers.add(HEADER_STRING, TOKEN_PREFIX+recentToken.getRefreshToken());
        
        HttpEntity request = new HttpEntity(headers);
        URI uri = new URI(BASE_URL + "/users//refreshed-token");
        
        ResponseEntity<AuthToken> response = restTemplate.exchange(uri, HttpMethod.GET, request, AuthToken.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(recentToken.getRefreshToken());
        assertThat(response.getBody().getToken()).isNotNull();
    }

    private ResponseEntity<User> getResponseOfRegisterUserQuery() {
        ResponseEntity<User> response = null;
        try {
            User u = userDao.findByUsername(STANDARD_USER.getUsername());
            if (u == null) {
                response = registerStandardUser(STANDARD_USER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private ResponseEntity<User> registerStandardUser(UserDto userDto) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI(BASE_URL + "/users/new");

        HttpEntity<UserDto> request = new HttpEntity<UserDto>(userDto, headers);

        return restTemplate.exchange(uri, HttpMethod.POST, request, User.class);

    }
}
