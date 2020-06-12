/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.util.MailSSLSocketFactory;
import com.ted.commander.server.model.ServerInfo;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by pete on 11/17/2014.
 */
@EnableWebMvc
@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan("com.ted.commander.server")
@PropertySource(value = "file:commander.properties", ignoreResourceNotFound = true)
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;

//    @Bean
//    public EmbeddedServletContainerCustomizer containerCustomizer() {
//
//        return (container -> {
//            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
//            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
//            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
//            container.addErrorPages(error401Page, error404Page, error500Page);
//        });
//    }


    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                System.out.println("Setting Cookie to Secure");
                servletContext.getSessionCookieConfig().setSecure(true);
                servletContext.getSessionCookieConfig().setHttpOnly(true);
                servletContext.getSessionCookieConfig().setMaxAge(0);
                System.out.println("servletContext.getSessionCookieConfig().isSecure" + servletContext.getSessionCookieConfig().isSecure() + " " + servletContext.getSessionCookieConfig().isHttpOnly());
            }
        };
    }


     @Bean
    public SessionTrackingConfigListener sessionTrackingConfigListener() {
        SessionTrackingConfigListener listener = new SessionTrackingConfigListener();
        return listener;
    }


    @Bean
    ServerInfo serverInfo() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerName(env.getProperty("server.name", "commander.theenergydetective.com"));
        serverInfo.setServerPort(Integer.parseInt(env.getProperty("server.port", "80")));
        serverInfo.setUseHttps(Boolean.parseBoolean(env.getProperty("server.useHttps", "false")));
        serverInfo.setPostDelay(Integer.parseInt(env.getProperty("server.postDelay", "1")));
        serverInfo.setHighPrecision(Boolean.parseBoolean(env.getProperty("server.useHighPrecision", "true")));
        serverInfo.setTimezone(env.getProperty("server.timezone", "US/Eastern"));
        serverInfo.setFromAddress(env.getProperty("server.fromAddress", "donotreply@commander.theenergydetective.com"));
        return serverInfo;
    }


    @Bean
    JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

//        javaMailSender.setHost(env.getProperty("smtp.host", "localhost"));
//        javaMailSender.setPort(Integer.parseInt(env.getProperty("smtp.port", "25")));



        javaMailSender.setHost(env.getProperty("smtp.host", "smtp.mailgun.org"));
        javaMailSender.setPort(465);
        javaMailSender.setProtocol("smtps");
        javaMailSender.setUsername("<INSERT USERNAME HERE>");
        javaMailSender.setPassword("<INSERT PASSWORD HERE>");
        Properties properties = new Properties();
        properties.setProperty("mail.debug", "false");
        properties.setProperty("mail.smtps.ssl.trust", "*");

        try {
            MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
            socketFactory.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.socketFactory", socketFactory);
            properties.put("mail.smtps.ssl.socketFactory", socketFactory);
        } catch (Exception ex){
            LoggerFactory.getLogger(this.getClass().getName()).error("Error setting SSL socket factory", ex);
        }

        javaMailSender.setJavaMailProperties(properties);


        return javaMailSender;
    }

    @Bean
    public VelocityEngine velocityEngine() throws VelocityException, IOException {
        VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader." +
                        "ClasspathResourceLoader");
        factory.setVelocityProperties(props);

        return factory.createVelocityEngine();
    }


    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        return om;
    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        converters.add(mappingJackson2HttpMessageConverter);
//        converters.add(new StringHttpMessageConverter()); // THIS WAS MISSING
//    }
}
