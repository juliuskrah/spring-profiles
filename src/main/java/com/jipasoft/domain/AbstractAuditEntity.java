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

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 * 
 * @author Julius Krah
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Field("created_by")
	@Size(min = 1, max = 50)
	@Column(updatable = false)
	private String createdBy;

	@Field("created_date")
	@Column(nullable = false)
	private ZonedDateTime createdDate = ZonedDateTime.now();

	@Field("last_modified_by")
	@Size(max = 50)
	private String lastModifiedBy;

	@Field("last_modified_date  ")
	private ZonedDateTime lastModifiedDate;
}
