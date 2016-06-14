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
package com.jipasoft.repository.mysql;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jipasoft.repository.BaseRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * An implemetation of the {@link BaseRepository} interface for the
 * {@code mysql} profile
 * 
 * @author Julius Krah
 *
 * @param <T>
 *            The entity type
 * @param <ID>
 *            The entity identifier
 */
@Slf4j
public class BaseRepositoryImpl<T, ID extends Serializable> implements BaseRepository<T, ID> {
	private Class<T> persistentClass;

	@Inject
	protected Provider<SessionFactory> sessionFactory;

	//@formatter:off
	public BaseRepositoryImpl() {}
	//@formatter:on

	public BaseRepositoryImpl(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(T entity) {
		this.sessionFactory.get().getCurrentSession().saveOrUpdate(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(T entity) {
		Session session = this.sessionFactory.get().getCurrentSession();
		session.delete(session.contains(entity) ? entity : session.merge(entity));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAll() {
		Session session = this.sessionFactory.get().getCurrentSession();
		Query query = session.createQuery(String.format("DELETE FROM %s e", persistentClass.getSimpleName()));
		log.debug("Query executed: {}", String.format("DELETE FROM %s e", persistentClass.getSimpleName()));
		query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		Session session = this.sessionFactory.get().getCurrentSession();
		Criteria criteria = session.createCriteria(persistentClass);

		return criteria.list();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<T> findOneById(ID id) {
		Session session = this.sessionFactory.get().getCurrentSession();

		return Optional.of((T) session.get(persistentClass, id));
	}

}
