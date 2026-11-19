package br.com.lobogang.vacancy_manager.modules.candidates.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.config.SwaggerConfig;
import br.com.lobogang.vacancy_manager.modules.candidates.dto.ApplyJobRequestDTO;
import br.com.lobogang.vacancy_manager.modules.candidates.dto.ApplyJobResponseDTO;
import br.com.lobogang.vacancy_manager.modules.candidates.useCases.ApplyJobCandidateUseCase;
import br.com.lobogang.vacancy_manager.modules.candidates.useCases.ListJobsByFilterUseCase;
import br.com.lobogang.vacancy_manager.modules.company.dto.JobResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidate/job")
@PreAuthorize("hasRole('CANDIDATE')")
@SecurityRequirement(name = SwaggerConfig.CANDIDATE_AUTH)
@Tag(name = "Candidate jobs", description = "Search vacancies and apply")
public class CandidateJobController {

  @Autowired
  private ListJobsByFilterUseCase listJobsByFilterUseCase;

  @Autowired
  private ApplyJobCandidateUseCase applyJobCandidateUseCase;

  @GetMapping
  @Operation(summary = "Search vacancies by description (case-insensitive); no filter returns all")
  public List<JobResponseDTO> findJobs(@RequestParam(required = false) String filter) {
    return this.listJobsByFilterUseCase.execute(filter).stream()
        .map(JobResponseDTO::from)
        .toList();
  }

  @PostMapping("/apply")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Apply the logged-in candidate to a vacancy")
  public ApplyJobResponseDTO apply(@Valid @RequestBody ApplyJobRequestDTO body, @RequestAttribute("candidate_id") UUID candidateId) {
    var application = this.applyJobCandidateUseCase.execute(candidateId, body.jobId());
    return ApplyJobResponseDTO.from(application);
  }
}
