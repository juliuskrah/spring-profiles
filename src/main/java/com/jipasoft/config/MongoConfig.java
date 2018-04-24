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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.jipasoft.domain.util.JSR310DateConverters;
import com.jipasoft.domain.util.JSR310DateConverters.DateToLocalDateConverter;
import com.jipasoft.domain.util.JSR310DateConverters.DateToLocalDateTimeConverter;
import com.jipasoft.domain.util.JSR310DateConverters.DateToZonedDateTimeConverter;
import com.jipasoft.domain.util.JSR310DateConverters.LocalDateTimeToDateConverter;
import com.jipasoft.domain.util.JSR310DateConverters.LocalDateToDateConverter;
import com.jipasoft.domain.util.JSR310DateConverters.ZonedDateTimeToDateConverter;
import com.jipasoft.repository.mongo.BaseRepositoryImpl;
import com.jipasoft.util.Profiles;

/**
 * Configuration specific for {@code mongo} profile. This configuration uses
 * Spring Data MongoDb. The underlying datastore is MongoDb document store
 * 
 * @author Julius Krah
 *
 */
@Profile(Profiles.MONGO)
@Configuration
@EnableMongoRepositories(basePackageClasses = BaseRepositoryImpl.class)
public class MongoConfig {

	/**
	 * Register converters for java.time.*<br/>
	 * MongoDB has difficulty understanding the new Java Time API
	 * 
	 * @return CustomConversions the Converters
	 * @see JSR310DateConverters
	 */
	@Bean
	public MongoCustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(DateToZonedDateTimeConverter.INSTANCE);
		converters.add(ZonedDateTimeToDateConverter.INSTANCE);
		converters.add(DateToLocalDateConverter.INSTANCE);
		converters.add(LocalDateToDateConverter.INSTANCE);
		converters.add(DateToLocalDateTimeConverter.INSTANCE);
		converters.add(LocalDateTimeToDateConverter.INSTANCE);
		return new MongoCustomConversions(converters);
	}

}
