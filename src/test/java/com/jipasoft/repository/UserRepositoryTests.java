/*
* Copyright 2016, Julius Krah
* by the @authors tag. See the LICENCE in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.jipasoft.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.jipasoft.config.ApplicationTests;
import com.jipasoft.domain.User;
import com.jipasoft.util.Profiles;

@ActiveProfiles(Profiles.MONGO)
@Transactional
public class UserRepositoryTests extends ApplicationTests {
	private static final Logger log = LoggerFactory.getLogger(UserRepositoryTests.class);
	@Autowired
	private UserRepository userRepository;

	@Before
	public void setUp() {
		userRepository.deleteAll();

		User user = new User();
		user.setEmail("juliuskrah@gmail.com");
		user.setLogin("julius");
		user.setResetKey("aw55asa7d5Sdcs8dAsa8");
		user.setCreatedBy("System");
		user.setPassword("$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG");

		userRepository.save(user);
	}

	@Test
	public void testFindByLogin() {
		User user = userRepository.findOneByLogin("julius").get();
		assertNotNull(user);
		log.debug("user: {} was created on {} with id {}", user.getLogin(), user.getCreatedDate(), user.getId());
		assertThat(user.getLogin(), is("julius"));
	}

	@Test
	public void testUpdate() {
		Optional<User> user = userRepository.findOneByEmail("juliuskrah@gmail.com");
		assertNotNull(user);
		log.debug("updating user: {} with email: {} and id: {} created on: {}", user.get().getLogin(), user.get().getEmail(),
				user.get().getId(), user.get().getCreatedDate());

		User u = user.get();
		u.setEmail("JuliusKrah@hotmail.com");
		u.setLastModifiedDate(ZonedDateTime.now());

		userRepository.save(u);

		u = null;
		u = userRepository.findOneByEmail("JuliusKrah@hotmail.com").get();
		assertNotNull(u);

		assertThat(u.getEmail(), is("JuliusKrah@hotmail.com"));
		log.debug("updated user: {}'s info. user id: {}, user email: {}, date created: {}, date modified {}", u.getLogin(), u.getId(),
				u.getEmail(), u.getCreatedDate(), u.getLastModifiedDate());
	}

	@Test
	public void testFindOneByResetKey() {
		User user = userRepository.findOneByLogin("julius").get();
		assertNotNull(user);
		user.setResetKey("aw55asa7d5Sdcs7dAsa8");
		user.setFirstName("Julius");
		user.setLastName("Krah");
		user.setResetDate(ZonedDateTime.now());

		userRepository.save(user);
		user = null;

		user = userRepository.findOneByResetKey("aw55asa7d5Sdcs7dAsa8").get();
		assertThat(user.getResetKey(), is("aw55asa7d5Sdcs7dAsa8"));

		log.debug("user: {} with id {} requested reset key: {} on {}", user.getLogin(), user.getId(), user.getResetKey(),
				user.getResetDate());
	}

	@Test
	public void testDelete() {
		User user = userRepository.findOneByLogin("julius").get();
		assertNotNull(user);

		userRepository.delete(user);
		user = userRepository.findOneByLogin("julius").orElse(null);
		assertNull(user);
	}

	@Test
	public void testFindById() {
		User user = userRepository.findOneByLogin("julius").get();
		assertNotNull(user);

		User userById = userRepository.findOneById(user.getId()).get();
		assertNotNull(userById);
		assertThat(user.getId(), is(userById.getId()));
	}
}
