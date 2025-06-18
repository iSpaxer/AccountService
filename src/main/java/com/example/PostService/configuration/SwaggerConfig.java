package com.example.PostService.configuration;

import com.example.PostService.dto.LoginRequest;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {

        var openApi = new OpenAPI()
                .info(new Info().title("Your API").version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("JWT", // todo ?
                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                );


        return openApi;

    }

    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin_panel")
                .addOpenApiCustomizer(openApi -> {
                    ModelConverters.getInstance().read(LoginRequest.class)
                            .forEach(openApi.getComponents()::addSchemas);

                    openApi
                            .path("/api/jwt/login", new PathItem()
                                    .post(new Operation()
                                            .summary("Вход в админ панель.")
                                            .addTagsItem("Admin")
                                            .requestBody(new RequestBody()
                                                    .content(new Content().addMediaType(
                                                                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                    new MediaType().schema(new Schema<LoginRequest>()
                                                                            .$ref("#/components/schemas/LoginRequest"))
                                                            )
                                                    )
                                            )
                                            .responses(new ApiResponses()
                                                    .addApiResponse("201", new ApiResponse()
                                                            .description("Successful login!")
                                                            .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                    new MediaType().schema(new Schema<String>().example("Login successful")))))
                                                    .addApiResponse("400", new ApiResponse()
                                                            .description("Bad request"))
                                                    .addApiResponse("500", new ApiResponse()
                                                            .description("INNER SERVER ERROR"))
                                            )
                                    ))
                            .path("/api/jwt/refresh", new PathItem()
                                    .post(new Operation()
                                            .summary("Получить новый access и refresh токен.")
                                            .addTagsItem("Admin")
                                            .requestBody(new RequestBody()
                                                    .content(new Content().addMediaType(
                                                                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                    new MediaType().schema(new Schema().addProperty("refresh", new Schema<String>().description("Refresh token")))
                                                            )
                                                    )
                                            )
                                            .responses(new ApiResponses()
                                                    .addApiResponse("200", new ApiResponse()
                                                            .description("Successful login!")
                                                            .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                                                    new MediaType().schema(new Schema<String>().example("Login successful")))))
                                                    .addApiResponse("400", new ApiResponse()
                                                            .description("Bad request"))
                                                    .addApiResponse("500", new ApiResponse()
                                                            .description("INNER SERVER ERROR"))
                                            )
                                    ));

                })
                .packagesToScan("ru.dv.web.controller.user")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Admins API").version("1.0.0")))
                .build();
    }


}

