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
package com.jipasoft.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jipasoft.service.AccountService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexConntroller {
	@NonNull
	private final AccountService accountService;

	@Autowired
	private DataSourceProperties prop;

	@PostConstruct
	public void init() {
		log.info("The embedded datasource connection string is: {}", prop.getUrl());
	}

	@RequestMapping(path = { "/", "/index" }, method = GET)
	public String index(Model model) {
		model.addAttribute("users", accountService.findAll());

		return "index";
	}

	@RequestMapping(path = "/login", method = GET)
	public String login(Model model) {
		model.addAttribute("users", accountService.findAll());

		return "signin";
	}

}
