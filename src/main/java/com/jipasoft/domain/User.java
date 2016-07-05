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
package com.jipasoft.domain;

import static javax.persistence.GenerationType.AUTO;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "account")
@ToString(exclude = { "password", "authorities" })
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractAuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = AUTO)
	private Integer id;

	@NotNull
	@Pattern(regexp = "^[a-z0-9]*$|(anonymousUser)")
	@Size(min = 1, max = 50)
	@Column(unique = true)
	private String login;

	
	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash")
	@JsonIgnore
	private String password;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(unique = true)
	private String email;

	@NotNull
	private boolean activated = false;

	@Size(max = 20)
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@JsonIgnore
	private String resetKey;

	private ZonedDateTime resetDate = null;

	@ManyToMany
	//@formatter:off
	@JoinTable(name = "user_role", inverseJoinColumns = {
			@JoinColumn(name = "role_name")
		}, joinColumns = {
			@JoinColumn(name = "account_id")
		}
	)
	//@formatter:on
	private Set<Authority> authorities;
}
