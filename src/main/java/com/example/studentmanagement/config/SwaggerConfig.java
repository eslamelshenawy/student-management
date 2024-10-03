package com.example.studentmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlers;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // Enable Swagger 2 documentation
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2) // Configure Swagger 2
                .select()
                .apis(RequestHandlers.basePackage("com.example.studentmanagement.controller")) // Specify the base package for controllers
                .paths(PathSelectors.any()) // All endpoints will be documented
                .build();
    }
}
