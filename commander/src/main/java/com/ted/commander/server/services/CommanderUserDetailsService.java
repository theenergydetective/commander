/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.services;

import com.ted.commander.common.enums.UserState;
import com.ted.commander.server.model.CommanderUser;
import com.ted.commander.server.repository.CommanderUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class CommanderUserDetailsService implements UserDetailsService {

    static final Logger LOGGER = LoggerFactory.getLogger(CommanderUserDetailsService.class);

    @Autowired
    private CommanderUserRepository commanderUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("loadUserByUsername: {}", username);
        CommanderUser commanderUser = commanderUserRepository.findByEmail(username);
        if (commanderUser == null) {
            LOGGER.warn("UNKNOWN USER: {}", username);
            throw new UsernameNotFoundException("Username " + username + " not found");
        }

        LOGGER.debug("FOUND USER: {}", commanderUser.toString());

        //Check the state
        if (!commanderUser.getState().equals(UserState.ENABLED)) {
            LOGGER.warn("DISABLED STATE: {}", commanderUser);
            throw new UsernameNotFoundException("Invalid state for user: " + username + " :" + commanderUser.getState());
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_BASIC"));

        if (commanderUser.getAdminRole()) {
            LOGGER.info("ADMIN USER: {}", username);
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
//        Collection<? extends GrantedAuthority> authorities;
//        authorities = Arrays.asList(() -> "ROLE_BASIC");

        return new User(username, commanderUser.getPassword(), authorities);
    }
}
