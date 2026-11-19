package br.com.lobogang.vacancy_manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private static final String[] SWAGGER_LIST = {
      "/swagger-ui/**",
      "/swagger-ui.html",
      "/v3/api-docs/**"
  };

  @Autowired
  private SecurityFilter securityFilter;

  @Autowired
  private SecurityCandidateFilter securityCandidateFilter;

  @Bean // Bean define que o Spring vai gerenciar a instância do objeto retornado por
        // esse método
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        // API sem sessão: cada requisição se autentica pelo token
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Sem token (ou token inválido) -> 401. Token válido sem a role certa -> 403.
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(auth -> {
          // Cadastro e login são públicos; o resto exige token
          auth.requestMatchers(HttpMethod.POST, "/candidate/", "/company/").permitAll()
              .requestMatchers("/company/auth", "/candidate/auth").permitAll()
              .requestMatchers(SWAGGER_LIST).permitAll()
              // /error precisa ser público, senão um erro 400/500 vira 403 sem corpo
              .requestMatchers("/error").permitAll();
          auth.anyRequest().authenticated();
        })
        .addFilterBefore(securityCandidateFilter, BasicAuthenticationFilter.class)
        .addFilterBefore(securityFilter, BasicAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
