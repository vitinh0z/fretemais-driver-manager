package com.fretemais.drivermanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Motoristas")
                        .version("1.0")
                        .description("Esta API permite o gerenciamento completo de motoristas, incluindo operações de criação, leitura, atualização e exclusão (CRUD), além de filtragem avançada.")
                        .contact(new Contact()
                                .name("Suporte FreteMais")
                                .email("suporte@fretemais.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}