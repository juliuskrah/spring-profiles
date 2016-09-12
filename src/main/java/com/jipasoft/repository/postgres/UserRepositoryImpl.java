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
package com.jipasoft.repository.postgres;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.jipasoft.domain.User;
import com.jipasoft.repository.UserRepository;

/**
 * An implementation of {@link UserRepository} which uses {@link EntityManager}.
 * This is used for the {@code postgres} profile
 * 
 * @author Julius Krah
 *
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User, String> implements UserRepository {

	public UserRepositoryImpl() {
		super(User.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findOneByResetKey(String resetKey) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.resetKey LIKE :resetKey");
		query.setParameter("resetKey", resetKey);
		return Optional.ofNullable((User) query.getSingleResult());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findOneByEmail(String email) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.email LIKE :email");
		query.setParameter("email", email);
		return Optional.ofNullable((User) query.getSingleResult());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findOneByLogin(String login) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.login LIKE :login");
		query.setParameter("login", login);
		return Optional.ofNullable((User) query.getSingleResult());
	}

}
