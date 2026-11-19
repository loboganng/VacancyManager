package br.com.lobogang.vacancy_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

  // Nomes usados em @SecurityRequirement nos controllers
  public static final String COMPANY_AUTH = "jwt_company";
  public static final String CANDIDATE_AUTH = "jwt_candidate";

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Vacancy Manager API")
            .description("Companies post vacancies; candidates search and apply. "
                + "Each user type has its own JWT: get a token in /company/auth or /candidate/auth "
                + "and paste it in the matching scheme under Authorize.")
            .version("1.0"))
        .components(new Components()
            .addSecuritySchemes(COMPANY_AUTH, bearer("Token from POST /company/auth"))
            .addSecuritySchemes(CANDIDATE_AUTH, bearer("Token from POST /candidate/auth")));
  }

  private SecurityScheme bearer(String description) {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description(description);
  }
}
