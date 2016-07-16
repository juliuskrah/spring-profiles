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

import static com.jipasoft.domain.util.JSR310DateConverters.*;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.github.mongobee.Mongobee;
import com.jipasoft.repository.mongo.BaseRepositoryImpl;
import com.jipasoft.util.Profiles;
import com.mongodb.Mongo;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration specific for {@code mongo} profile. This configuration uses
 * Spring Data MongoDb. The underlying datastore is MongoDb document store
 * 
 * @author Julius Krah
 *
 */
@Slf4j
@Profile(Profiles.MONGO)
@Configuration
@Import(value = MongoAutoConfiguration.class)
@EnableMongoRepositories(basePackageClasses = BaseRepositoryImpl.class)
public class MongoConfig {
	@Autowired
	private MongoProperties properties;
	@Inject
	private Mongo mongo;

	@Bean
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(DateToZonedDateTimeConverter.INSTANCE);
		converters.add(ZonedDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		return new CustomConversions(converters);
	}

	@Bean
	public Mongobee mongobee() {
		log.debug("Configuring Mongobee");
		Mongobee mongobee = new Mongobee(mongo);
		mongobee.setDbName(properties.getDatabase());
		// package to scan for migrations
		mongobee.setChangeLogsScanPackage("com.jipasoft.config.dbmigrations");
		mongobee.setEnabled(true);
		return mongobee;
	}
}
