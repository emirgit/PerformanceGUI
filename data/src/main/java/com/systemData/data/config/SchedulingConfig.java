package com.systemData.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


/**
 * Scheduling Config class
 * to return net RestTemplate
 */
@Configuration
public class SchedulingConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
