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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jipasoft.domain.AbstractAuditEntity;
import com.jipasoft.service.Services;
import com.jipasoft.web.Controllers;

/**
 * Application root configuration. The
 * {@link SpringBootApplication @SpringBootApplication} <br />
 * is a convenience annotation for {@link ComponentScan @ComponentScan},
 * {@link Configuration @Configuration}, and <br />
 * {@link EnableAutoConfiguration @EnableAutoConfiguration}. The
 * {@code scanBasePackageClasses} in this context is type safe.
 * 
 * @see H2Config
 * @see PostgresConfig
 * @see MySQLConfig
 * 
 * @author Julius Krah
 *
 */
@SpringBootApplication(scanBasePackageClasses = { Controllers.class, Services.class })
@EntityScan(basePackageClasses = AbstractAuditEntity.class)
@Import(value = { H2Config.class, PostgresConfig.class, MySQLConfig.class })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}
