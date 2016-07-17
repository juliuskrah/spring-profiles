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
package com.jipasoft.config.dbmigrations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.jipasoft.util.Profiles;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import lombok.extern.slf4j.Slf4j;

/**
 * This a class annotated with {@link ChangeLog @ChangeLog} that tells Mongobee
 * that it's a changelog class<br/>
 * Database migrations are created here
 * 
 * @author Julius Krah
 *
 */
@Slf4j
@Profile(Profiles.MONGO)
@ChangeLog(order = "001")
@SuppressWarnings("unchecked")
public class InitialSetupMigration {

	private Map<String, String>[] authoritiesUser = new Map[] { new HashMap<>() };

	private Map<String, String>[] authoritiesAdminAndUser = new Map[] { new HashMap<>(), new HashMap<>() };

	{
		authoritiesUser[0].put("_id", "ROLE_USER");
		authoritiesAdminAndUser[0].put("_id", "ROLE_USER");
		authoritiesAdminAndUser[1].put("_id", "ROLE_ADMIN");
	}

	@ChangeSet(order = "01", author = "julius", id = "01-addRoles")
	public void addRoles(DB db) {
		log.info("Inserting document into 'role'...");
		// Get the 'role' collection
		DBCollection authorityCollection = db.getCollection("role");
		//@formatter:off
		authorityCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "ROLE_ADMIN")
				.get()
		);
		authorityCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "ROLE_USER")
				.get()
		);
		log.info("Acquired changelog on 'role'");
		//@formatter:on
	}

	@ChangeSet(order = "02", author = "julius", id = "02-addAccounts")
	public void addAccounts(DB db) {
		log.info("Inserting document into 'account'...");
		// Get the 'account' collection
		DBCollection usersCollection = db.getCollection("account");
		// Set a unique constraint on 'login' field
		usersCollection.createIndex(new BasicDBObject("login", 1), "login", true);
		// Set a unique constraint on 'email' field
		usersCollection.createIndex(new BasicDBObject("email", 1), "email", true);
		//@formatter:off
		usersCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "user-0")
				.add("login", "system")
				.add("password", "$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG")
				.add("first_name", "")
				.add("last_name", "System")
				.add("email", "system@localhost")
				.add("activated", true)
				.add("created_by", "system")
				.add("created_date", new Date())
				.add("authorities", authoritiesAdminAndUser)
				.get()
		);
		usersCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "user-1")
				.add("login", "anonymousUser")
				.add("password", "$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO")
				.add("first_name", "Anonymous")
				.add("last_name", "User")
				.add("email", "anonymous@localhost")
				.add("activated", true)
				.add("created_by", "system")
				.add("created_date", new Date())
				.add("authorities", new Map[] {})
				.get()
		);
		usersCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "user-2")
				.add("login", "admin")
				.add("password", "$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC")
				.add("first_name", "admin")
				.add("last_name", "Administrator")
				.add("email", "admin@localhost")
				.add("activated", true)
				.add("created_by", "system")
				.add("created_date", new Date())
				.add("authorities", authoritiesAdminAndUser)
				.get()
		);
		usersCollection.insert(BasicDBObjectBuilder.start()
				.add("_id", "user-3")
				.add("login", "user")
				.add("password", "$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K")
				.add("first_name", "")
				.add("last_name", "User")
				.add("email", "user@localhost")
				.add("activated", true)
				.add("created_by", "system")
				.add("created_date", new Date())
				.add("authorities", authoritiesUser)
				.get()
		);
		log.info("Acquired changelog on 'account'");
		//@formatter:on
	}
}
