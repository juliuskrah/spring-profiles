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

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import com.jipasoft.repository.mysql.BaseRepositoryImpl;
import com.jipasoft.util.Profiles;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration specific to the {@code mysql} profile. This configuration uses
 * Hibernate specific methods for JPA connection. The underlying datastore is
 * MySQL
 * 
 * @author Julius Krah
 *
 */
@Slf4j
@Configuration
@Profile(Profiles.MYSQL)
@ComponentScan(basePackageClasses = BaseRepositoryImpl.class)
public class MySQLConfig {
	/**
	 * Create a session factory bean to be injected for ths profile. The MySQL
	 * profile uses Hibernate natively.
	 * 
	 * @return
	 */
	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory() {
		log.debug("Starting SessionFactory bean");
		return new HibernateJpaSessionFactoryBean();
	}

	/**
	 * Register filters akin to: <code>
	 * <pre>
	 * &lt;filter&gt;
	 *   &lt;filter-name&gt;openSessionInView&lt;/filter-name&gt;
	 *   &lt;filter-class&gt;org.springframework.orm.hibernate5.support.OpenSessionInView&lt;/filter-class&gt;
	 * &lt;/filter&gt;
	 * &lt;filter-mapping&gt;
	 *   &lt;filter-name&gt;openSessionInView&lt;/filter-name&gt;
	 *   &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
	 * &lt;/filter-mapping&gt;
	 * </pre>
	 * </code>
	 * 
	 * @return {@code FilterRegistrationBean} the registered filters
	 */
	@Bean
	public FilterRegistrationBean filterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(openSessionInView());
		registration.addUrlPatterns("/*");

		return registration;
	}

	/**
	 * Create a filter as a bean in Spring Boot
	 * 
	 * @return
	 */
	@Bean
	public Filter openSessionInView() {
		return new OpenSessionInViewFilter();
	}
}
