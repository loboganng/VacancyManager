package br.com.lobogang.vacancy_manager.modules.company.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.modules.company.entities.CompanyEntity;
import br.com.lobogang.vacancy_manager.modules.company.useCases.CreateCompanyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/company")
@Tag(name = "Company", description = "Company sign-up and login")
public class CompanyController {

  @Autowired
  private CreateCompanyUseCase createCompanyUseCase;

  // Erros (usuário duplicado, validação) são tratados no ExceptionHandlerController
  @PostMapping("/")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sign up a company")
  public CompanyEntity create(@Valid @RequestBody CompanyEntity companyEntity) {
    return this.createCompanyUseCase.execute(companyEntity);
  }
}
