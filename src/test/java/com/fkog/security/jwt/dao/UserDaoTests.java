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
		toBeSaved.setUsername("acgh");

		User savedUser = null;
		if (userDao.findByUsername(toBeSaved.getUsername()) == null) {

			savedUser = userDao.save(toBeSaved);
		} else {
			savedUser = toBeSaved;
		}

		System.err.println(savedUser);
		assertThat(savedUser.getLastLoggedOut()).isNull();

		userDao.updateLastLoggedOut(new Date(), savedUser.getUsername());
		User loggedOutUser = userDao.findByUsername(toBeSaved.getUsername());
		System.err.println(loggedOutUser);

		assertThat(loggedOutUser.getLastLoggedOut()).isNotNull();

	}
}
