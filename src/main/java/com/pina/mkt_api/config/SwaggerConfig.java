package com.pina.mkt_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API Pi.Na - Gestão de Projetos")
                        .version("1.0")
                        .description("""
                                API de gerenciamento de marketing e projetos (Pi.Na).
                                
                                🔐 Autenticação:
                                Esta API utiliza JWT (Bearer Token) para acesso aos endpoints protegidos.
                                
                                Fluxo obrigatório:
                                1. Registrar usuário (POST /api/users/register)
                                2. Realizar login (POST /api/users/login)
                                3. Copiar o token retornado
                                4. Clicar em "Authorize" no Swagger e inserir:
                                   Bearer {seu_token}
                                5. Utilizar os demais endpoints autenticados
                                """))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}