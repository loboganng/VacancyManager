package br.com.lobogang.vacancy_manager.modules.candidates.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.config.SwaggerConfig;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.dto.ProfileCandidateResponseDTO;
import br.com.lobogang.vacancy_manager.modules.candidates.useCases.CreateCandidateUseCase;
import br.com.lobogang.vacancy_manager.modules.candidates.useCases.ProfileCandidateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidate")
@Tag(name = "Candidate", description = "Candidate sign-up, login and profile")
public class CandidateController {

  // Injetamos o UseCase de criação de candidato, para que possamos utilizá-lo no endpoint de criação
  @Autowired
  private CreateCandidateUseCase createCandidateUseCase;

  @Autowired
  private ProfileCandidateUseCase profileCandidateUseCase;

  @PostMapping("/")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sign up a candidate")
  public CandidateEntity create(@Valid @RequestBody CandidateEntity candidateEntity) {
    return this.createCandidateUseCase.execute(candidateEntity);
  }

  @GetMapping("/")
  @PreAuthorize("hasRole('CANDIDATE')") // Apenas usuários com a role "CANDIDATE" podem acessar este endpoint
  @SecurityRequirement(name = SwaggerConfig.CANDIDATE_AUTH)
  @Operation(summary = "Profile of the logged-in candidate")
  public ProfileCandidateResponseDTO get(@RequestAttribute("candidate_id") UUID candidateId) {
    return this.profileCandidateUseCase.execute(candidateId);
  }
}
