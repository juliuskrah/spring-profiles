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

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;

/**
 * The CRUD operations performed on the database with common methods
 * 
 * @author Julius Krah
 *
 * @param <T>
 *            The enetity type
 * @param <ID>
 *            The entity Identifier
 */
public interface BaseRepository<T, ID extends Serializable> {

	/**
	 * Saves or Updates an {@code entity} to the datastore. Depending on the
	 * {@link Profile} selected a different underlying datastore implementation
	 * is used
	 * 
	 * @param entity
	 *            the entity to {@code save} or {@code update}
	 */
	public void save(T entity);

	/**
	 * Deletes an {@code entity} from the underlying datastore
	 * 
	 * @param entity
	 *            the entity to detach
	 */
	public void delete(T entity);

	/**
	 * Deletes all records from the {@code Entity} table
	 */
	public void deleteAll();

	/**
	 * Gets all entities from the database
	 * 
	 * @return List<T> list of entities returned
	 */
	public List<T> findAll();

	/**
	 * Retrieves an {@code entity} entity from the underlying datastore by its
	 * id
	 * 
	 * @param id
	 *            the entity identifier
	 * @return an entity
	 */
	public Optional<T> findOneById(ID id);
}
