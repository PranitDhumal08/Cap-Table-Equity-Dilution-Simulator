package com.captablex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI capTableXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CapTableX API – Startup Cap Table & Funding Round Dilution Simulator")
                        .description("Production-quality financial API for managing startup capitalization tables, " +
                                "calculating precise equity ownership, and simulating venture funding rounds.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CapTableX Engineering Team")
                                .email("engineering@captablex.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
