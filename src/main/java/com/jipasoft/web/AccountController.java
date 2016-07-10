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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jipasoft.domain.User;
import com.jipasoft.domain.dto.UserDTO;
import com.jipasoft.service.AccountService;
import com.jipasoft.util.AjaxUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class AccountController {
	@NonNull
	private final AccountService accountService;
	@NonNull
	private final PasswordEncoder encoder;
	@NonNull
	private final MessageSource messageSource;
	private static String ADD_USER_VIEW_NAME = "add_user";

	@GetMapping(path = { "add", "signup" })
	public String add(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		model.addAttribute(new UserDTO());
		if (AjaxUtils.isAjaxRequest(requestedWith)) {
			return ADD_USER_VIEW_NAME.concat(" :: signupForm");
		}
		return ADD_USER_VIEW_NAME;
	}

	@GetMapping(path = "update/{id}")
	public String update(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
			@PathVariable int id) {
		Optional<User> user = accountService.findAccountById(id);
		UserDTO userDTO = new UserDTO();

		if (user.isPresent()) {
			User u = user.get();

			userDTO.setPassword(u.getPassword());
			userDTO.setId(u.getId());
			userDTO.setLogin(u.getLogin());
			userDTO.setFirstName(u.getFirstName());
			userDTO.setLastName(u.getLastName());
			userDTO.setEmail(u.getEmail());
			userDTO.setActivated(u.isActivated());
		}
		model.addAttribute(userDTO);
		if (AjaxUtils.isAjaxRequest(requestedWith)) {
			return ADD_USER_VIEW_NAME.concat(" :: signupForm");
		}
		return ADD_USER_VIEW_NAME;
	}

	@PostMapping("add")
	public String add(@Valid @ModelAttribute UserDTO userDTO, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return ADD_USER_VIEW_NAME;
		}
		accountService.save(userDTO.createUser(encoder));
		log.debug("Saved user: {}", userDTO.createUser(encoder));
		ra.addFlashAttribute("message", "create.add").addFlashAttribute("name", userDTO.getLogin());

		return "redirect:/";
	}

	@PatchMapping("add")
	public String update(@Valid @ModelAttribute UserDTO userDTO, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return ADD_USER_VIEW_NAME;
		}
		Optional<User> user = accountService.findAccountById(userDTO.getId());
		if (user.isPresent()) {
			User u = user.get();
			u.setLogin(userDTO.getLogin());
			u.setFirstName(userDTO.getFirstName());
			u.setLastName(userDTO.getLastName());
			u.setEmail(userDTO.getEmail());
			u.setActivated(userDTO.isActivated());
			u.setLastModifiedBy(userDTO.getLogin());
			u.setLastModifiedDate(ZonedDateTime.now());

			log.debug("Updating user: {}", u);
			accountService.save(u);
		}

		ra.addFlashAttribute("message", "create.update").addFlashAttribute("name", userDTO.getLogin());
		return "redirect:/";
	}

	@ResponseBody
	@DeleteMapping("delete/{id}")
	public String delete(@PathVariable int id, Locale loc) {
		Optional<User> user = accountService.findAccountById(id);
		if (user.isPresent()) {
			accountService.deleteAccount(user.get());
			return messageSource.getMessage("create.delete", new Object[] { user.get().getLogin() }, loc);
		}
		return String.format("No user with id: %s found", id);
	}

	@ResponseBody
	@GetMapping("find_all")
	public List<User> users() {
		return accountService.findAll();
	}
}
