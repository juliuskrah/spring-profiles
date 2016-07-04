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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.jipasoft.service.AccountService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class IndexConntroller {
	@NonNull
	private final AccountService accountService;

	@PostConstruct
	public void init() {

	}

	@GetMapping({ "/", "/index" })
	public String index() {
		log.debug("Rendering index page...");

		return "index";
	}

	@GetMapping("/login")
	public String login() {
		log.debug("Rendering signin page...");

		return "signin";
	}

}
