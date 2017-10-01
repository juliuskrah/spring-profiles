[![Build Status](https://travis-ci.org/juliuskrah/spring-profiles.svg?branch=master)](https://travis-ci.org/juliuskrah/spring-profiles)

# Spring Profiles Project
The Spring Profiles project demonstates how you can setup a Spring Boot project that connects to a variety
of backend databases (Relational and NoSQL). This uses the `Profiles` provided by the Spring Framework.

In this regard you can easily switch between backends when deploying the web application using a variety of
mechanisms. The supported mechanisms are:  
1.  CommandLine arguments
2.  Configuration file settings (`.properties` and `.yaml`)
3.  Programmatic setup
4.  JVM properties
5.  System environment properties

With the exception of the programmatic setup, you can switch profiles easily without changing source code and 
re-compiling.  

The supported backends used in this project are:  
1.  H2 database
2.  MySQL database
3.  PostgreSQL database
4.  MongoDB database

You can extend this simple application to support any backend of your choice by implementing 
`com.jipasoft.repository.UserRepository`.

## Quick Start
This section contains the pre-requisite to run the application, how to run the application and how to use some of the application features.

## Live Demo
There is a live demo running on Heroku.

<http://heroku.juliuskrah.com/>

**Note**: This may take up to 120 seconds on first request.  
**Login**: `admin/admin`

### Deploy on Heroku
To deploy Spring Profiles on Heroku, click the button below:

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

### Pre-requisite
Not much is needed to run this application. You only need two things:

*Required*

* [Git][]
* [JDK][] 8+

*Optional*

* [MySQL][]
* [PostgreSQL][]
* [MongoDB][]
* [Maven][] 3.3+

### Getting the Project
Get the project from the source repository
>`git clone https://juliuskrah@bitbucket.org/juliuskrah/spring-profiles.git`

### Running the Project
To run the project, first navigate into the source directory `cd spring-profiles` and execute `mvnw`.  
When you execute the `mvnw` command, two things are happening here.

* `spring-boot:run`: first it executes the Spring-Boot maven plugin
* `--spring.profiles.active=h2`: second it starts the project with the `h2` profile active; Passing it as commandline arguments

With this in mind, you don't have to worry about setting up a database. The application starts an undertow server instance on port `8080`.
> [`http://localhost:8080`][1]

Use the link above to access the application. The application is secured with [Spring Security][security] and you would be redirected to a login page [`http://localhost:8080/login`][2]
> username: `admin`  
  password: `admin`

It's that easy.

### Application Features
This section lists all the features of this application

#### Internationalization

file: `src/main/java/com/jipasoft/config/Application.java`

```java
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

public class Application extends WebMvcConfigurerAdapter {
 	...
 	
 	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver clr = new CookieLocaleResolver();
		clr.setDefaultLocale(Locale.US);
		return clr;
	}
 	
 	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang"); // The language will change with this request parameter
		return lci;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}
```

You tell Spring where to find the message sources for i18n  
file: `src/main/resources/config/application.yml`

```yaml
spring:
  messages:
    basename: i18n/messages
```

You define the messages with their keys and values  
file: `src/main/resources/i18n/messages.properties`

```properties
...
user.add=Add user
user.update=Update user
user.login=Login
user.firstname=First name
```

This is used in Thymeleaf 3 leveraging it's i18n support  
file: `src/main/resources/templates/fragments/header.html`

```html
<!DOCTYPE html>
<html>
  <head>...</head>
  <body>
  ...
    <ul>
      <th:block sec:authorize="isAuthenticated()">
        <li>
          <a href="/logout" th:href="@{/logout}" th:text="#{home.logout}">Sign out</a>
        </li>
        <li>
          <a sec:authentication="name">Bob</a>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
            aria-expanded="false">[[#{lang.name}]] <span class="caret"></span>
          </a>
          <ul class="dropdown-menu">
            <li role="presentation">
              <a role="menuitem" tabindex="-1" href="?lang=en" th:text="#{lang.en}">en</a>
            </li>
            <li role="presentation">
              <a role="menuitem" tabindex="-1" href="?lang=fr" th:text="#{lang.fr}">fr</a>
            </li>
          </ul>
        </li>
      </th:block>
    </ul>
  </body>
</html>
```

*English* 
![Homepage English](https://i.imgur.com/yohfSns.png)

*French*  
![Homepage French](https://i.imgur.com/YT7O8o9.png)


#### Error Handling
This project leverages `Spring-Boot`'s error handler using convention over configuration  
`src\main\resources\templates\error\404.html`

#### Bean Validation
This application uses `JSR 303`.  
file: `src/main/java/com/jipasoft/domain/dto/UserDTO.java`

```java
import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class UserDTO {
	private String id;

	@NotNull
	@Pattern(regexp = "^[a-z0-9]*$|(anonymousUser)")
	@Size(min = 1, max = 100)
	private String login;

	@NotNull
	@Size(min = 1, max = 60)
	private String password;

	@NotNull
	@Size(min = 1, max = 50)
	private String firstName;

	@NotNull
	@Size(min = 1, max = 50)
	private String lastName;

	@Email
	@NotNull
	@Size(min = 1, max = 100)
	private String email;

	@NotNull
	private boolean activated = false;

	@NotNull
	private String createdBy = "system";

	@NotNull
	private ZonedDateTime createdDate = ZonedDateTime.now();

	// Standard getters and setters
}

```

file: `src/main/java/com/jipasoft/web/AccountController.java`

```java
import javax.validation.Valid;

import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jipasoft.domain.dto.UserDTO;

public class AccountController {
	private static String ADD_USER_VIEW_NAME = "add_user";
	
	...
	 
	@PostMapping("add")
	public String add(@Valid @ModelAttribute UserDTO userDTO, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return ADD_USER_VIEW_NAME;
		}
		...

		return "redirect:/";
	}
}
```

User validation  
![User Validation](https://i.imgur.com/BzQwTi8.png)


#### Database Migration
The application makes use of database migration to track database changes. The migrations are split into two depending on which profile
is active. 

1.   SQL Migration
2.   NoSQL Migration

##### SQL Migration
The sql migration uses `Liquibase`.

file: `src/main/java/com/jipasoft/config/Application.java`

```java
import javax.sql.DataSource;
import javax.inject.Inject;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import liquibase.integration.spring.SpringLiquibase;

@SpringBootApplication
@EnableConfigurationProperties(LiquibaseProperties.class)
public class Application {
	@Inject
	private LiquibaseProperties liquibaseProperties;
	...

	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(liquibaseProperties.getChangeLog());
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		liquibase.setShouldRun(liquibaseProperties.isEnabled());

		return liquibase;
	}
}
```

##### NoSQL Migration
The nosql migration uses `MongoBee`.

file: `src/main/java/com/jipasoft/config/Application.java`

```java
import javax.inject.Inject;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.github.mongobee.Mongobee;

@SpringBootApplication
@EnableConfigurationProperties(LiquibaseProperties.class)
public class Application {
	@Inject
	private MongoProperties mongoProperties;
	...

	@Bean
	public Mongobee mongobee() {
		Mongobee mongobee = new Mongobee(mongo);
		mongobee.setDbName(mongoProperties.getDatabase());
		mongobee.setChangeLogsScanPackage("com.jipasoft.config.dbmigrations");
		mongobee.setEnabled(true);
		return mongobee;
	}
}
```

#### Runs on Multiple Database Platforms
The application can run on:

1.   H2
2.   PostgreSQL
3.   MySQL
4.   MongoDB

Continue reading to see how it is achieved.

#### Ajax
In building a web application, you may not always want rebuild the `DOM` to display changes to your end user. Sometimes you just want
to pool your backend for tiny data to update the `DOM`. In this sample, I will build a table row with ajax using [JQuery][].

```javascript
$.ajax({
  url : "user/find_all",
  success : function(response) {
    $("#delete").hide();
    $("tr:has(td)").remove();

    $.each(response,
      function(i, item) {
        $('<tr>').append(
          $('<td>').append(
            $('<a>').attr(
              {
                href : '#',
                onclick : "update('user/update/" + item.id + "')",
                'data-toggle' : 'modal',
                'data-target' : '#myModal'
              }
            ).text(item.login)),
          $('<td>').text(item.firstName),
          $('<td>').text(item.lastName),
          $('<td>').text(item.email),
          $('<td>').text(item.activated),
          $('<td>').text(item.createdBy),
          $('<td>').text(item.createdDate),
          $('<td>').append(
            $('<button>').attr(
              {
                onclick : "deleteUser('user/delete/" + item.id + "', this)"
              }).addClass('btn btn-danger glyphicon glyphicon-remove')
          )
        )
      .appendTo('tbody');
    });
  }
});
```

## Technology Stack
* [Spring-Boot][]
* [Hibernate][]
* [Spring Data MongoDB][]
* [Thymeleaf][]
* [Jackson Datatype][]
* [JQuery][]
* [MongoBee][]
* [Liquibase][]
* [Spring Data JPA][]
* [Spring Security][security]
* [Java Mail][]
* [Project Lombok][]
* [Undertow Server][]

## Introduction
[Spring][] [Profiles][] provide a way to segregate parts of your application configuration and make it only available in certain environments. Any [`@Component`][Component] or [`@Configuration`][Configuration] can be marked with [`@Profile`][Profile] to limit when it is loaded:

file: `src/main/java/com/jipasoft/config/H2Config.java`

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

file: `src/main/java/com/jipasoft/repository/mysql/UserRepositoryImpl.java`

```java
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

file: `src/main/java/com/jipasoft/repository/postgres/UserRepositoryImpl.java`

```java
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

This bean, if loaded together with the above bean of same type and name, it will cause a conflict. Thus this bean definition will only get loaded if the `postgres` profile is active.

## [H2][]
The H2 profile is the default profile for this application if no active profile is selected. This profile uses [Spring Data JPA][] 
as an abstraction of the [Hibernate][] JPA implementation.

## [MongoDB][]
This profile can be activated if you have MongoDB installed. To run with this profile:  
`mvnw spring-boot:run -Drun.arguments="--spring.profiles.active=mongo"`

## [MySQL][]
This profile can be activated if you have MySQL installed. To run with this profile:  
`mvnw spring-boot:run -Drun.arguments="--spring.profiles.active=mysql"`

## [PostgreSQL][]
This profile can be activated if you have PostgreSQL installed. To run with this profile:  
`mvnw spring-boot:run -Drun.arguments="--spring.profiles.active=postgres"`

## Heroku
This profile can be activated to deploy on Heroku. To enable the heroku profile:

file: `Procfile`

```json
web: java -jar target/*.war --spring.profiles.active=heroku --server.port=$PORT
```


## Aspect
The application is configured to send email to an administrator with this key: `spring.user.email` when an exception occurs. There are two ways to configure this.
In order to avoid overflow of email to the administrator, we will create an asynchronous mail sender implementation:

file: `src/main/java/com/jipasoft/task/AsyncMailSender.java`

```java
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

public class AsyncMailSender extends JavaMailSenderImpl {

	@Async
	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		super.send(simpleMessage);
	}

	@Async
	@Override
	public void send(SimpleMailMessage... simpleMessages) throws MailException {
		super.send(simpleMessages);
	}
}

```

Notice the `@Async` annotation on the `send()` methods. In order to activate them for spring, you would need to
enable it with with `@EnableAsync` on a `@configuration` class:

file: `src/main/java/com/jipasoft/config/AspectConfig.java`

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.jipasoft.util.Profiles;

@Profile(Profiles.ASPECT)
@EnableAsync
@Configuration
public class AspectConfig {
	...
	
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(5);
		taskExecutor.setMaxPoolSize(25);
		taskExecutor.setQueueCapacity(100);

		return taskExecutor;
	}
	
	...
}
```
From the above configuration you would notice the `taskExecutor` bean. For the `@EnableAsync` annotation to properly
function, you need a taskExecutor bean for Asynchronous execution.

To get started with the aspect profile, set the `spring.mail.username` and `spring.mail.password` 
properties in the `application-aspect.yml` file. If your mail server is not Gmail, set and configure your 
`spring.mail.host` and `spring.mail.port` accordingly. 

### Method 1 (Using `BeanNameAutoProxyCreator`)

file: `src/main/java/com/jipasoft/config/AspectConfig.java`

```java
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.jipasoft.task.AsyncMailSender;
import com.jipasoft.task.ExceptionInterceptor;

@EnableAsync
@Configuration
public class AspectConfig {
	...
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new AsyncMailSender();
		...
		return sender;
	}

	@Bean
	public ExceptionInterceptor exceptionInterceptor() {
		return new ExceptionInterceptor();
	}

	@Bean
	public BeanNameAutoProxyCreator autoProxyCreater() {
		BeanNameAutoProxyCreator autoProxyCreator = new BeanNameAutoProxyCreator();
		autoProxyCreator.setBeanNames("*Controller");
		autoProxyCreator.setInterceptorNames("exceptionInterceptor");

		return autoProxyCreator;
	}
}
```
We have our `mailSender` bean that is an asynchronous implementation of `MailSender`. The next bean configured is the 
`exceptionInterceptor` that intercepts calls on an interface on its way to the target. These are nested "on top" of the target.
To put it all together, we have the `autoProxyCreater` bean of type `BeanNameAutoProxyCreator`. In this bean we are 
telling Spring to scan all bean names ending in `*.Controller` and setting the interceptor as the `exceptionInterceptor`
bean created earlier.

### Method 2 (Using `AspectJ`)
file: `src/main/java/com/jipasoft/task/ExceptionAspect.java`

```java
import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionAspect {

	@Inject
	private MailSender mailSender;
	

	@Pointcut("within(com.jipasoft.web..*)") // Proxy all methods in the controller classes
	public void mailingPointcut() {
	}

	@AfterThrowing(pointcut = "mailingPointcut()", throwing = "e")
	public void mailAfterThrowing(JoinPoint joinPoint, Throwable e) {
		// Application Logic
	}
}
```

Next we scan our `Aspect` class through `ComponentScan` in our configuration class.

```java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import com.jipasoft.util.Profiles;

@EnableAsync
@EnableAspectJAutoProxy
@Profile(Profiles.ASPECT)
@Configuration
@ComponentScan(basePackageClasses = ExceptionAspect.class)
public class AspectConfig {
	// Configured beans
}

```
Voila you are done :smile:.

[comment]: # (The implicit link name shortcut allows you to omit the name of the link, in which case the link text itself is used as the name)
[comment]: # (Reference links are not case sensitive)

[1]: http://localhost:8080
[2]: http://localhost:8080/login "Login to Spring-Profiles"
[Maven]: http://maven.apache.org/ "Maven"
[Git]: https://git-scm.com/ "Git"
[MongoBee]: https://github.com/mongobee/mongobee/wiki/How-to-use-mongobee "MongoBee database migration tool"
[Spring-Boot]: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/ "Spring Boot"
[Jackson Datatype]: https://github.com/FasterXML/jackson-datatype-jsr310
[Thymeleaf]: http://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html
[JQuery]: https://api.jquery.com
[Java Mail]: https://java.net/projects/javamail/pages/Home
[Project Lombok]: http://jnb.ociweb.com/jnb/jnbJan2010.html
[Liquibase]: http://www.liquibase.org/documentation/index.html
[Undertow Server]: http://undertow.io/undertow-docs/undertow-docs-1.3.0/index.html
[JDK]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[security]: http://docs.spring.io/spring-security/site/docs/4.1.1.RELEASE/reference/htmlsingle/
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
[MongoDB]: https://docs.mongodb.com/?_ga=1.1231921.1865610331.1455481105
[Spring]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/ "Spring Framework"
[Profiles]: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html
[Configuration]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
[Profile]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Profile.html
[Component]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html
