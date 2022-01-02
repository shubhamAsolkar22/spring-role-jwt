package com.fkog.security.jwt.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fkog.security.jwt.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserDaoTests {

	@Autowired
	private UserDao userDao;
	
	@Test
	public void testLogoutUpdate() {
		User toBeSaved = new User();
		toBeSaved.setEmail("ach@s.com");
		toBeSaved.setBusinessTitle("A");
		toBeSaved.setName("acgh");
		toBeSaved.setPassword("erwer");
		toBeSaved.setPhone("1234567890");
		
		User savedUser = userDao.save(toBeSaved);
		
		assertThat(savedUser.getLastLoggedOut()).isNull();
		
		userDao.updateLastLoggedOut(new Date(),savedUser.getId());
		
		Optional<User> loggedOutUser = userDao.findById(savedUser.getId());
		
		assertThat(loggedOutUser.map(u->u.getLastLoggedOut()).isPresent()).isTrue();
		
		
		
	}
}
