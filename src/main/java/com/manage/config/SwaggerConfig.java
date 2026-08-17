package com.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI employeeOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management System API")
                        .version("1.0")
                        .description("REST APIs for Employee Management System"));
    }
}