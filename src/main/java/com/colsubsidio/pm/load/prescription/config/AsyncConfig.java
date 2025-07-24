/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author Camilo Olivo
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Value( "${task.executor.corePoolSize:100}")
    private int corePoolSize;

    @Value( "${task.executor.queueCapacity:10}" )
    private int queueCapacity;
    
    @Bean( name = "dequeueProcess" )
    public ThreadPoolTaskExecutor myTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize( corePoolSize );
        executor.setQueueCapacity( queueCapacity );
        executor.initialize();
        return executor;
    }
}
