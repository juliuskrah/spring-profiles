package com.jipasoft.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Authority implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Size(min = 1, max = 50)
	private String name;
}
