package com.jipasoft.web;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
public class AccountController {
	@NonNull
	private final AccountService accountService;
	@NonNull
	private final PasswordEncoder encoder;
	private static String ADD_USER_VIEW_NAME = "add_user";

	@RequestMapping(path = { "add", "signup" }, method = GET)
	public String add(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		model.addAttribute(new UserDTO());
		if (AjaxUtils.isAjaxRequest(requestedWith)) {
			return ADD_USER_VIEW_NAME.concat(" :: signupForm");
		}
		return ADD_USER_VIEW_NAME;
	}

	@RequestMapping(path = "update/{id}", method = GET)
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

	@RequestMapping(value = "add", method = POST)
	public String add(@Valid @ModelAttribute UserDTO userDTO, Errors errors) {
		if (errors.hasErrors()) {
			return ADD_USER_VIEW_NAME;
		}
		accountService.save(userDTO.createUser(encoder));
		log.debug("Saved user: {}", userDTO.createUser(encoder));
		
		return "redirect:/";
	}

	@RequestMapping(value = "add", method = PATCH)
	public String update(@Valid @ModelAttribute UserDTO userDTO, Errors errors) {
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
		return "redirect:/";
	}

	@ResponseBody
	@RequestMapping(path = "delete/{id}", method = DELETE)
	public String delete(@PathVariable int id) {
		Optional<User> user = accountService.findAccountById(id);
		if (user.isPresent()) {
			accountService.deleteAccount(user.get());
			return String.format("User %s successfully deleted", user.get().getLogin());
		}
		return String.format("No user with id: %s found", id);
	}
}
