package com.studyhelper.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApi(@Value("${spring.application.version:undefined}") String version) {
        return new OpenAPI().info(getInfo(version)).addServersItem(new Server().url("/"));
    }

    private Info getInfo(String version) {
        var info = new Info();
        info.setTitle("StudyHelper");
        info.setDescription("Приложение для взаимопомощи студентам ПГУ");
        info.setVersion(version);
        return info;
    }
}
