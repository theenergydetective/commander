/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;


@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(OAuthServerConfig.class);

    @Autowired
    DataSource commanderDataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public TokenStore authTokenStore() {
        JdbcTokenStore jdbcTokenStore =  new JdbcTokenStore(commanderDataSource);
        jdbcTokenStore.setAuthenticationKeyGenerator(new UniqueAuthenticationKeyGenerator());
        return jdbcTokenStore;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws
            Exception {

        AuthenticationManager customAM = new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                LOGGER.debug(authenticationManager.toString());
                LOGGER.debug(authenticationManager.getClass().getName());
                return authenticationManager.authenticate(authentication);
            }
        };

        endpoints
                .authenticationManager(customAM)
                .tokenStore(authTokenStore());


    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(commanderDataSource);
    }

}
