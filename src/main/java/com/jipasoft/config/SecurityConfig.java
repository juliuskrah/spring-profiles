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

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Enable spring security for this application that handles Authorization and
 * Authentication
 * 
 * @author Julius Krah
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Inject
	private Provider<UserDetailsService> userDetailsServiceProvider;
	@Inject
	private PasswordEncoder passwordEncoder;

	/**
	 * Inject a global parent for Spring Authentication Manager.
	 * 
	 * @param auth
	 * @throws Exception
	 * @see AuthenticationManager
	 */
	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceProvider.get()).passwordEncoder(passwordEncoder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		// @formatter:off
		web.ignoring()
			.antMatchers(HttpMethod.OPTIONS, "/**")
			// don't secure static resources
			.antMatchers("/js/**")
			.antMatchers("/css/**")
			.antMatchers("/h2/**");
		// @formatter:on
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http.authorizeRequests()
			.antMatchers("/user/signup", "/user/add").permitAll()
			.antMatchers("/**").authenticated()
		.and()
			.formLogin()
			.loginPage("/login").permitAll()
		.and()
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
		// @formatter:on
	}

}
