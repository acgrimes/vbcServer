package com.dd.vbc.business.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ServiceConfig {

    @Bean
    ThreadPoolExecutor executor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }
}
