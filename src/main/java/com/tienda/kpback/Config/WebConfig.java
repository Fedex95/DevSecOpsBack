package com.tienda.kpback.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${IP_FRONT}")
    private String IP_FRONT;

    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://172.18.0.4:80", "http://172.18.0.5:80", "http://172.18.0.2:80",
                    "http://172.18.0.3:80", "http://172.18.0.6:80", "http://172.18.0.7:80"
                ) 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
                .allowCredentials(true);
    }
}