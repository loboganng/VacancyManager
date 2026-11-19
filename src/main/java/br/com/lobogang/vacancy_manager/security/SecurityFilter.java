package br.com.lobogang.vacancy_manager.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.lobogang.vacancy_manager.providers.JWTProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  private JWTProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Colocamos a autenticação como nula para garantir que o objeto esteja "limpo"
    // no início de cada requisição
    // SecurityContextHolder.getContext().setAuthentication(null);

    // Pegando o token do header da requisição
    String header = request.getHeader("Authorization");

    // O login é público: um token velho no header não pode impedir de logar de novo
    if (request.getRequestURI().startsWith("/company") && !request.getRequestURI().equals("/company/auth")) {
      if (header != null) {
        // Chamando o método validateToken do JWTProvider para validar o token
        var token = this.jwtProvider.validateToken(header);
        // If token in empty/invalid, return 401
        if (token == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }

        var roles = token.getClaim("roles").asList(Object.class);
        var grants = roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
            .toList();
        ;

        request.setAttribute("company_id", token.getSubject());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(token.getSubject(), null,
            grants);

        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }

    // Continuar a execução da requisição se o token for válido ou se não houver
    // token (para rotas públicas)
    filterChain.doFilter(request, response);
  }
}
