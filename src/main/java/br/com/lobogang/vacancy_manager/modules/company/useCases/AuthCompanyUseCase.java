package br.com.lobogang.vacancy_manager.modules.company.useCases;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.lobogang.vacancy_manager.exceptions.InvalidCredentialsException;
import br.com.lobogang.vacancy_manager.modules.company.dto.AuthCompanyDTO;
import br.com.lobogang.vacancy_manager.modules.company.dto.AuthCompanyResponseDTO;
import br.com.lobogang.vacancy_manager.modules.company.repositories.CompanyRepository;

@Service
public class AuthCompanyUseCase {

  // Para verificar se a empresa existe, checamos o email e a senha, para isso
  // precisamos do repositório da empresa
  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("${security.token.secret}") // @Value serve para injetar o valor do application.properties
  private String secretKey;

  public AuthCompanyResponseDTO execute(AuthCompanyDTO authCompanyDTO) {
    // Verificando se a empresa existe ou não, e lançando um erro
    var company = this.companyRepository.findByUsername(authCompanyDTO.getUsername()).orElseThrow(InvalidCredentialsException::new);

    // Veriricando se as senhas são iguais
    // Recebemos a senha do login e a senha salva no banco de dados
    var passwordMatches = this.passwordEncoder.matches(authCompanyDTO.getPassword(), company.getPassword());

    // Se não for igual -> Erro
    if (!passwordMatches) {
      throw new InvalidCredentialsException();
    }
    // Se for igual -> Gerar token
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    var expiresIn = Instant.now().plus(Duration.ofHours(2));

    var token = JWT.create().withIssuer("javagas")
        .withExpiresAt(expiresIn)
        .withSubject(company.getId().toString())
        .withClaim("roles", Arrays.asList("COMPANY"))
        .sign(algorithm);

    var authCompnayResponseDTO = AuthCompanyResponseDTO.builder()
        .access_token(token)
        .expires_in(expiresIn.toEpochMilli())
        .build();

    return authCompnayResponseDTO;

  }
}
