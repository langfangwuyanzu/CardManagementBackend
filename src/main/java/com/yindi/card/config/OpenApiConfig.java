package com.yindi.card.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Yolŋu Card API")           // 这里改成你们的名字
                        .description("API documentation for the Yolŋu Card Management System")
                        .version("1.0.0")
                );
    }
};
