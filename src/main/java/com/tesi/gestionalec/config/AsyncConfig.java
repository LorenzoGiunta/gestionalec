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

    /**
     * Pool dedicato all'invio email (chiamate SMTP — operazione di rete lenta).
     * Usato da EmailService.inviaEmail() con @Async("emailExecutor").
     */
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

    /**
     * Pool dedicato alla propagazione delle notifiche agli Observer.
     * Separato dall'emailExecutor: un picco di notifiche DB non blocca le email
     * e viceversa. Usato da DatabaseNotificaObserver e EmailNotificaObserver
     * con @Async("notificaExecutor").
     */
    @Bean(name = "notificaExecutor")
    public Executor notificaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // notifiche sono frequenti ma leggere
        executor.setMaxPoolSize(10);    // picco: più pratiche aggiornate in parallelo
        executor.setQueueCapacity(100); // buffer generoso — nessuna notifica persa
        executor.setThreadNamePrefix("notifica-thread-");
        executor.initialize();
        return executor;
    }
}