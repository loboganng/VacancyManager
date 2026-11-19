package br.com.lobogang.vacancy_manager.modules.candidates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.modules.candidates.dto.AuthCandidateRequestDTO;
import br.com.lobogang.vacancy_manager.modules.candidates.dto.AuthCandidateResponseDTO;
import br.com.lobogang.vacancy_manager.modules.candidates.useCases.AuthCandidateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/candidate")
@Tag(name = "Candidate", description = "Candidate sign-up, login and profile")
public class AuthCandidateController {

  @Autowired
  private AuthCandidateUseCase authCandidateUseCase;

  // Credencial inválida vira 401 no ExceptionHandlerController
  @PostMapping("/auth")
  @Operation(summary = "Log in as a candidate and get a candidate JWT (valid for 10 minutes)")
  public AuthCandidateResponseDTO auth(@RequestBody AuthCandidateRequestDTO authCandidateRequestDTO) {
    return this.authCandidateUseCase.execute(authCandidateRequestDTO);
  }
}
