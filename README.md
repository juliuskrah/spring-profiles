# Spring Profiles Project
[Spring][] [Profiles][] provide a way to segregate parts of your application configuration and make it only available in certain environments. Any [`@Component`][Component] or [`@Configuration`][Configuration] can be marked with [`@Profile`][Profile] to limit when it is loaded:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.jipasoft.repository.h2.BaseRepositoryImpl;

@Configuration
@Profile("h2")
@EnableJpaRepositories(basePackageClasses = BaseRepositoryImpl.class)
public class H2Config {

}
```

The focus of this project is to demonstrate how to build a Spring application that runs on multiple database platforms. Profiles will be used in this project to segregate the various configurations. Each Database is configured to use a different persistence strategy. The [RDBMS][] databases will use [JPA][] and the [NoSQL][] database will use [Spring Data MongoDB][]. The database technologies targeted are:

* [H2][]
* [MySQL][]
* [PostgreSQL][]
* [MongoDB][]

All profiles implement the interfaces in `com.jipasoft.repository` package using different strategies. Another use for the profiles is to prevent conflicting [bean][] definitions. With the profiles configured properly, not all beans will be loaded together. e.g.

```java
package com.jipasoft.repository.mysql;

import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.jipasoft.domain.User;
import com.jipasoft.repository.UserRepository;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User, Integer> implements UserRepository {

	public UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public Optional<User> findOneByResetKey(String resetKey) {
		Session session = this.sessionFactory.get().getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.like("resetKey", resetKey));
		return Optional.of((User) criteria.uniqueResult());
	}

	@Override
	public Optional<User> findOneByEmail(String email) {
		Session session = this.sessionFactory.get().getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.like("email", email));
		return Optional.of((User) criteria.uniqueResult());
	}

	@Override
	public Optional<User> findOneByLogin(String login) {
		Session session = this.sessionFactory.get().getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.like("login", login));
		return Optional.of((User) criteria.uniqueResult());
	}

}

```

The above bean of type `com.jipasoft.repository.UserRepository` gets loaded only if the `mysql` profile is active.

The following bean is also of type `com.jipasoft.repository.UserRepository`. 

```java
package com.jipasoft.repository.postgres;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.jipasoft.domain.User;
import com.jipasoft.repository.UserRepository;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User, Integer> implements UserRepository {

	public UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public Optional<User> findOneByResetKey(String resetKey) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.resetKey LIKE :resetKey");
		query.setParameter("resetKey", resetKey);
		return Optional.of((User) query.getSingleResult());
	}
	
	@Override
	public Optional<User> findOneByEmail(String email) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.email LIKE :email");
		query.setParameter("email", email);
		return Optional.of((User) query.getSingleResult());
	}

	@Override
	public Optional<User> findOneByLogin(String login) {
		Query query = this.em.createQuery("SELECT u FROM User u WHERE u.login LIKE :login");
		query.setParameter("login", login);
		return Optional.of((User) query.getSingleResult());
	}

}

```

This bean, if loaded together with the above bean of same type and name will cause a conflict. Thus this bean definition will only get loaded if the `postgres` profile is active.

## [H2][]
The H2 profile is the default profile for this application if no active profile is selected. This profile uses [Spring Data JPA][] as an abstraction of the [Hibernate][] JPA implementation.


[comment]: # (The implicit link name shortcut allows you to omit the name of the link, in which case the link text itself is used as the name)
[comment]: # (Reference links are not case sensitive)

[bean]: http://www.tutorialspoint.com/spring/spring_bean_definition.htm "Spring Beans"
[Spring Data MongoDB]: http://docs.spring.io/spring-data/data-mongo/docs/1.9.2.RELEASE/reference/html/ "Spring Data MongoDB"
[Spring Data JPA]: http://docs.spring.io/spring-data/jpa/docs/1.10.2.RELEASE/reference/html/ "Spring Data JPA"
[Hibernate]: http://hibernate.org/orm/documentation/getting-started/ "Hibernate ORM"
[RDBMS]: http://www.tutorialspoint.com/sql/sql-rdbms-concepts.htm "Relational Database Management System"
[NoSQL]: http://nosql-database.org/ "Not Only SQL"
[JPA]: http://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm "Java Persistence API"
[H2]: http://www.h2database.com/html/quickstart.html "H2 database"
[MySQL]: http://dev.mysql.com/doc/ "MySQL database"
[PostgreSQL]: https://www.postgresql.org/docs/ "PostgreSQL database"
[MongoDb]: https://docs.mongodb.com/?_ga=1.1231921.1865610331.1455481105
[Spring]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/ "Spring Framework"
[Profiles]: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html
[Configuration]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
[Profile]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Profile.html
[Component]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html