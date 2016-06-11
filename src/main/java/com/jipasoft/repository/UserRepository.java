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

import java.util.Optional;

import com.jipasoft.domain.User;

/**
 * Repository interface for the {@link User} entity. It contains methods for
 * regular <code>CRUD</code> operations
 * 
 * @author Julius Krah
 *
 */
public interface UserRepository extends BaseRepository<User, Integer> {

	/**
	 * Retrieves a {@link User} entity from the underlying datastore by its
	 * ResetKey
	 * 
	 * @param resetKey
	 *            the resetKey
	 * @return a User entity
	 * @see User#getResetKey()
	 */
	public Optional<User> findOneByResetKey(String resetKey);

	/**
	 * Retrieves a {@link User} entity from the underlying datastore by its
	 * Email
	 * 
	 * @param email
	 *            the User's email
	 * @return a User entity
	 * @see User#getEmail()
	 */
	public Optional<User> findOneByEmail(String email);

	/**
	 * Retrieves a {@link User} entity from the underlying datastore by its
	 * login
	 * 
	 * @param login
	 *            the username
	 * @return a User entity
	 * @see User#getLogin()
	 */
	public Optional<User> findOneByLogin(String login);

}
