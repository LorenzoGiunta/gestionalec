package com.tesi.gestionalec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


// POOL DEI THREAD PER LIMITARE IL NUMERO DI THREAD CONTEMPORANEI CHE PUò LANCIARE IL SITO
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // thread sempre attivi
        executor.setMaxPoolSize(5);     // massimo in caso di picco
        executor.setQueueCapacity(50);  // email in coda se tutti occupati
        executor.setThreadNamePrefix("email-thread-");
        executor.initialize();
        return executor;
    }
}