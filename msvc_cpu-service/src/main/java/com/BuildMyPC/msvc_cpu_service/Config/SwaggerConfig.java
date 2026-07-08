package com.BuildMyPC.msvc_cpu_service.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String ESQUEMA = "bearer-jwt";

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("API Auths")
                        .version("1.0")
                        .description("Documentación de la API de gestión de auths"))
                .components(new Components().addSecuritySchemes(ESQUEMA,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)   // autenticacion por cabecera HTTP
                                .scheme("bearer")                 // formato "Authorization: Bearer <token>"
                                .bearerFormat("JWT")))
                // Aplica ese esquema a TODOS los endpoints: asi Swagger envia el token al ejecutar
                // "Try it out". Sin esto, con la seguridad activa toda prueba devolveria 401.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}