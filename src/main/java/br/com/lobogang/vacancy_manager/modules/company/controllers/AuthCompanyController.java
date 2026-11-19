package br.com.lobogang.vacancy_manager.modules.company.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.modules.company.dto.AuthCompanyDTO;
import br.com.lobogang.vacancy_manager.modules.company.dto.AuthCompanyResponseDTO;
import br.com.lobogang.vacancy_manager.modules.company.useCases.AuthCompanyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/company")
@Tag(name = "Company", description = "Company sign-up and login")
public class AuthCompanyController {

  @Autowired
  private AuthCompanyUseCase authCompanyUseCase;

  // Credencial inválida vira 401 no ExceptionHandlerController
  @PostMapping("/auth")
  @Operation(summary = "Log in as a company and get a company JWT (valid for 2 hours)")
  public AuthCompanyResponseDTO auth(@RequestBody AuthCompanyDTO authCompanyDTO) {
    return this.authCompanyUseCase.execute(authCompanyDTO);
  }
}
