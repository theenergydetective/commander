/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;


@Configuration
@EnableResourceServer
public class OAuthResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    DataSource commanderDataSource;

    @Autowired
    TokenStore authTokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("commanderAPI").tokenStore(authTokenStore);

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http
                .anonymous()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/error").permitAll()
                .antMatchers(HttpMethod.POST, "/api/password").permitAll()
                .antMatchers(HttpMethod.POST, "/api/alexa/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/password").permitAll()
                .antMatchers(HttpMethod.GET, "/api/version").permitAll()
                .antMatchers(HttpMethod.GET, "/api/version/haltServer").permitAll()
                .antMatchers(HttpMethod.GET, "/api/monte").permitAll()
                .antMatchers(HttpMethod.POST, "/api/tedPro/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/api/tedPro/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user").permitAll()
                .antMatchers(HttpMethod.GET, "/api/user/activate/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/download/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/billing/download/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/admin/playback/*").permitAll() //TODO: Remove this line
                .antMatchers(HttpMethod.GET, "/api/admin/st0pS3rv3r").permitAll() //TODO: Remove this line
                .antMatchers(HttpMethod.POST, "/api/pvactivate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/pvpostData").permitAll()
                .antMatchers(HttpMethod.POST, "/api/activate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/postData").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.hasScope('api')")
                .antMatchers(HttpMethod.OPTIONS, "/api/**").access("#oauth2.hasScope('api')")
                .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.hasScope('api')")
                .antMatchers(HttpMethod.PUT, "/api/**").access("#oauth2.hasScope('api')")
                .antMatchers(HttpMethod.PATCH, "/api/**").access("#oauth2.hasScope('api')")
                .antMatchers(HttpMethod.DELETE, "/api/**").access("#oauth2.hasScope('api')");

    }
}

