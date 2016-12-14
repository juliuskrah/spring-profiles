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
package com.jipasoft.config;

import java.util.Locale;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.github.mongobee.Mongobee;
import com.jipasoft.domain.AbstractAuditEntity;
import com.jipasoft.service.Services;
import com.jipasoft.task.ExceptionAspect;
import com.jipasoft.util.Profiles;
import com.jipasoft.web.Controllers;
import com.mongodb.Mongo;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

/**
 * Application root configuration. The
 * {@link SpringBootApplication @SpringBootApplication} <br />
 * is a convenience annotation for {@link ComponentScan @ComponentScan},
 * {@link Configuration @Configuration}, and <br />
 * {@link EnableAutoConfiguration @EnableAutoConfiguration}. The
 * {@code scanBasePackageClasses} in this context is type safe.
 * <p>
 * The application can run on multiple profiles to support different types of
 * databases, relational and non-relational.<br />
 * In the current state, the application runs on:
 * <ol>
 * <li>{@link H2Config H2 Database}</li>
 * <li>{@link PostgresConfig PostgreSQL Database}</li>
 * <li>{@link MySQLConfig MySQL Database}</li>
 * <li>{@link MongoConfig MongoDB}</li>
 * </ol>
 * </p>
 * 
 * @see H2Config
 * @see PostgresConfig
 * @see MySQLConfig
 * @see MongoConfig
 * 
 * @author Julius Krah
 *
 */
@Slf4j
@SpringBootApplication(scanBasePackageClasses = { Controllers.class, Services.class, ExceptionAspect.class })
@EnableConfigurationProperties({ LiquibaseProperties.class, MailProperties.class })
@EnableAspectJAutoProxy
@EntityScan(basePackageClasses = AbstractAuditEntity.class)
@Import(value = { H2Config.class, PostgresConfig.class, MySQLConfig.class, MongoConfig.class, AspectConfig.class, SecurityConfig.class })
public class Application extends WebMvcConfigurerAdapter {
	@Inject
	private LiquibaseProperties liquibaseProperties;
	@Inject
	private MongoProperties mongoProperties;
	@Inject
	private Mongo mongo;
	@Inject
	private Environment env;

	/**
	 * Entry point of the application
	 * 
	 * @param args
	 *            The arguments passed in from the command line
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.run(args);
	}

	/**
	 * {@link PasswordEncoder} bean.
	 * 
	 * @return <b>{@code BCryptPasswordEncoder}</b> with strength (passed as
	 *         argument) the log rounds to use, between 4 and 31
	 */
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(10);
	}

	/**
	 * i18n support bean. The locale resolver being used is Cookie.<br />
	 * When locale is changed and intercepted by the
	 * {@link Application#localeChangeInterceptor localeChangeInterceptor}.
	 * <br />
	 * The new locale is stored in a Cookie and remains active even after
	 * session timeout<br />
	 * or session being invalidated
	 * <p>
	 * Set a fixed Locale to <em>US</em> that this resolver will return if no
	 * cookie found.
	 * </p>
	 * 
	 * @return {@code LocaleResolver}
	 * @see Application#localeChangeInterceptor
	 */
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver clr = new CookieLocaleResolver();
		clr.setDefaultLocale(Locale.US);
		return clr;
	}

	/**
	 * i18n bean support for switching locale through a request param. <br />
	 * Users who are authenticated can change their default locale to another
	 * when they pass in a<br />
	 * url (http://example.com/&lt;contextpath&gt;/<em>lang=&lt;locale&gt;</em>)
	 * 
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	/**
	 * SQL database migration. Liquibase keeps track of database changes
	 * 
	 * @param dataSource
	 * @return
	 */
	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(liquibaseProperties.getChangeLog());
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		// When the mongo profile is active, the migration is not required
		if (env.acceptsProfiles(Profiles.MONGO))
			liquibase.setShouldRun(false);
		else {
			liquibase.setShouldRun(liquibaseProperties.isEnabled());
			log.trace("Configuring Liquibase...");
		}

		return liquibase;
	}

	/**
	 * Database migration
	 * 
	 * @return Mongobee
	 */
	@Bean
	// activate this bean only if the active profiles have 'Mongo'
	@ConditionalOnExpression("#{environment.acceptsProfiles('" + Profiles.MONGO + "')}")
	public Mongobee mongobee() {
		log.trace("Configuring Mongobee...");
		Mongobee mongobee = new Mongobee(mongo);
		mongobee.setDbName(mongoProperties.getDatabase());
		// package to scan for migrations
		mongobee.setChangeLogsScanPackage("com.jipasoft.config.dbmigrations");
		// set spring environment to process @Profile on
		// 'com.jipasoft.config.dbmigrations.InitialSetupMigration'
		mongobee.setSpringEnvironment(env);
		mongobee.setEnabled(true);
		return mongobee;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("signin");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

}
