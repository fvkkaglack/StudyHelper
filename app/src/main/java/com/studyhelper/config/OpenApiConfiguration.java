package com.studyhelper.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApi(@Value("${spring.application.version:undefined}") String version) {
        return new OpenAPI()
                .info(getInfo(version))
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    private Info getInfo(String version) {
        var info = new Info();
        info.setTitle("StudyHelper");
        info.setDescription("Приложение для взаимопомощи студентам Полоцкого государственного университета (ПГУ). " +
                "Студенты обмениваются заданиями за звёзды, с выбором исполнителя из заявок.");
        info.setVersion(version);
        return info;
    }
}