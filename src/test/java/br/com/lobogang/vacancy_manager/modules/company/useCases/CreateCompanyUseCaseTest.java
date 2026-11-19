package br.com.lobogang.vacancy_manager.modules.company.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.lobogang.vacancy_manager.exceptions.UserFoundException;
import br.com.lobogang.vacancy_manager.modules.company.entities.CompanyEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.CompanyRepository;

@ExtendWith(MockitoExtension.class)
class CreateCompanyUseCaseTest {

  @InjectMocks
  private CreateCompanyUseCase createCompanyUseCase;

  @Mock
  private CompanyRepository companyRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private CompanyEntity company() {
    var company = new CompanyEntity();
    company.setUsername("acme");
    company.setEmail("rh@acme.test");
    company.setPassword("senha123456");
    return company;
  }

  @Test
  @DisplayName("Should not create a company when username or email is already taken")
  void shouldNotCreateDuplicatedCompany() {
    when(companyRepository.findByUsernameOrEmail("acme", "rh@acme.test"))
        .thenReturn(Optional.of(new CompanyEntity()));

    assertThatThrownBy(() -> createCompanyUseCase.execute(company()))
        .isInstanceOf(UserFoundException.class);
    verify(companyRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should hash the password before saving the company")
  void shouldHashPasswordBeforeSaving() {
    when(companyRepository.findByUsernameOrEmail("acme", "rh@acme.test")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("senha123456")).thenReturn("hashed");
    when(companyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    var saved = createCompanyUseCase.execute(company());

    assertThat(saved.getPassword()).isEqualTo("hashed");
  }
}
