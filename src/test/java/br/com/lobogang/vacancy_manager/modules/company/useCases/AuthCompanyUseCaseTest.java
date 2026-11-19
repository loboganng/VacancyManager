package br.com.lobogang.vacancy_manager.modules.company.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;

import br.com.lobogang.vacancy_manager.exceptions.InvalidCredentialsException;
import br.com.lobogang.vacancy_manager.modules.company.dto.AuthCompanyDTO;
import br.com.lobogang.vacancy_manager.modules.company.entities.CompanyEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.CompanyRepository;

@ExtendWith(MockitoExtension.class)
class AuthCompanyUseCaseTest {

  @InjectMocks
  private AuthCompanyUseCase authCompanyUseCase;

  @Mock
  private CompanyRepository companyRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    // Fora do Spring, o @Value não é preenchido: colocamos o segredo direto no campo
    ReflectionTestUtils.setField(authCompanyUseCase, "secretKey", "test-secret");
  }

  @Test
  @DisplayName("Should reject login when the username does not exist")
  void shouldRejectUnknownUsername() {
    when(companyRepository.findByUsername("ghost")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authCompanyUseCase.execute(new AuthCompanyDTO("senha123456", "ghost")))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  @DisplayName("Should reject login when the password is wrong")
  void shouldRejectWrongPassword() {
    var company = new CompanyEntity();
    company.setPassword("hashed");
    when(companyRepository.findByUsername("acme")).thenReturn(Optional.of(company));
    when(passwordEncoder.matches("errada12345", "hashed")).thenReturn(false);

    assertThatThrownBy(() -> authCompanyUseCase.execute(new AuthCompanyDTO("errada12345", "acme")))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  @DisplayName("Should return a token with the COMPANY role and the company id as subject")
  void shouldReturnCompanyToken() {
    var company = new CompanyEntity();
    company.setId(UUID.randomUUID());
    company.setPassword("hashed");
    when(companyRepository.findByUsername("acme")).thenReturn(Optional.of(company));
    when(passwordEncoder.matches("senha123456", "hashed")).thenReturn(true);

    var response = authCompanyUseCase.execute(new AuthCompanyDTO("senha123456", "acme"));

    var decoded = JWT.decode(response.getAccess_token());
    assertThat(decoded.getSubject()).isEqualTo(company.getId().toString());
    assertThat(decoded.getClaim("roles").asList(String.class)).containsExactly("COMPANY");
  }
}
