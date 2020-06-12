/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.config;

import com.ted.commander.server.services.PollingService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource(value = "file:commander.properties", ignoreResourceNotFound = true)
@EnableTransactionManagement
@EnableJpaRepositories("com.ted.commander.server.repository")
public class DatabaseConfig {

    @Autowired
    Environment env;

    @Bean
    public DataSource commanderDataSource() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        if (PollingService.NUMBER_ENERGYPOST_THREADS > 0) {
            ds.setMaximumPoolSize(300);
        } else {
            ds.setMaximumPoolSize(100);
        }

        ds.addDataSourceProperty("url", env.getProperty("jdbc.jdbcUrl", "jdbc:mysql://localhost:3306/commander"));

        ds.addDataSourceProperty("user", env.getProperty("jdbc.username", "commander"));
        ds.addDataSourceProperty("password", env.getProperty("jdbc.password", "password"));
        ds.addDataSourceProperty("cachePrepStmts", true);
        ds.addDataSourceProperty("prepStmtCacheSize", 250);
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        ds.addDataSourceProperty("useServerPrepStmts", false);
        return ds;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(commanderDataSource());
        em.setPackagesToScan(new String[]{"com.ted.commander.common.model", "com.ted.commander.server.model"});
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//        properties.setProperty("hibernate.cache.use_query_cache", "true");
//        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
//        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
//        properties.setProperty("javax.persistence.sharedCache.mode", "ENABLE_SELECTIVE");
//        properties.setProperty("hibernate.show_sql", "true");
//        properties.setProperty("hibernate.generate_statistics", "true");


        return properties;
    }
}
