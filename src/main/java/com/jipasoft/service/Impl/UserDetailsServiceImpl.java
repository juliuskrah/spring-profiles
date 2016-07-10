package com.jipasoft.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jipasoft.domain.User;
import com.jipasoft.exception.AccountNotActivatedException;
import com.jipasoft.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
	@Inject
	private Provider<UserRepository> userRepositoryProvider;

	@Override
	public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException, AccountNotActivatedException {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login.toLowerCase();
		Optional<User> userFromDatabase = userRepositoryProvider.get().findOneByLogin(lowercaseLogin);

		return userFromDatabase.map(user -> {
			if (!user.isActivated()) {
				throw new AccountNotActivatedException(String.format("User %s is not activated", lowercaseLogin));
			}
			List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
					.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
		}).orElseThrow(() -> new UsernameNotFoundException(String.format("User %s was not found in the database", lowercaseLogin)));
	}

}
