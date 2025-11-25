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
                .allowedOrigins("http://" + IP_FRONT, "http://" + IP_FRONT + ":80", "http://localhost:3000") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
                .allowCredentials(true);
    }
}