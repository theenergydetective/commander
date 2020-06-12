/*
 * Copyright 2015 The Energy Detective. All Rights Reserved.
 */

package com.ted.commander.server.config;

import com.ted.commander.server.services.PollingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

/**
 * Created by pete on 11/17/2014.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    final static Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

    @Override
    public Executor getAsyncExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

        //Total number of pollers + a few extra for Scheduled tasks. Keep this under the mysql connection pool size.
        int concurrentThreads = PollingService.NUMBER_ENERGYPOST_THREADS
                + PollingService.NUMBER_PLAYBACK_THREADS
                + PollingService.NUMBER_STANDALONE_THREADS
                + 10;

        executor.setConcurrencyLimit(concurrentThreads);


        executor.setThreadGroupName("TED-");


        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

}
