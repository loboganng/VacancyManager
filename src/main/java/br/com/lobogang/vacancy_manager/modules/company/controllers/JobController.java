package br.com.lobogang.vacancy_manager.modules.company.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.config.SwaggerConfig;
import br.com.lobogang.vacancy_manager.modules.company.dto.CreateJobDTO;
import br.com.lobogang.vacancy_manager.modules.company.dto.JobApplicationResponseDTO;
import br.com.lobogang.vacancy_manager.modules.company.dto.JobResponseDTO;
import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import br.com.lobogang.vacancy_manager.modules.company.useCases.CreateJobUseCase;
import br.com.lobogang.vacancy_manager.modules.company.useCases.ListJobApplicationsUseCase;
import br.com.lobogang.vacancy_manager.modules.company.useCases.ListJobsByCompanyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/company/job")
@PreAuthorize("hasRole('COMPANY')")
@SecurityRequirement(name = SwaggerConfig.COMPANY_AUTH)
@Tag(name = "Company jobs", description = "Vacancies of the logged-in company")
public class JobController {

  @Autowired
  private CreateJobUseCase createJobUseCase;

  @Autowired
  private ListJobsByCompanyUseCase listJobsByCompanyUseCase;

  @Autowired
  private ListJobApplicationsUseCase listJobApplicationsUseCase;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a vacancy for the logged-in company")
  public JobResponseDTO create(@Valid @RequestBody CreateJobDTO createJobDTO, @RequestAttribute("company_id") UUID companyId) {
    // Com o builder, ele cria uma instância de JobEntity, e já seta os atributos que estão no CreateJobDTO
    var jobEntity = JobEntity.builder()
        .benefits(createJobDTO.getBenefits())
        .companyId(companyId)
        .description(createJobDTO.getDescription())
        .level(createJobDTO.getLevel())
        .build();

    return JobResponseDTO.from(this.createJobUseCase.execute(jobEntity));
  }

  @GetMapping
  @Operation(summary = "List the vacancies of the logged-in company")
  public List<JobResponseDTO> listMine(@RequestAttribute("company_id") UUID companyId) {
    return this.listJobsByCompanyUseCase.execute(companyId).stream()
        .map(JobResponseDTO::from)
        .toList();
  }

  @GetMapping("/{id}/applications")
  @Operation(summary = "List the candidates who applied to one of the company's vacancies")
  public List<JobApplicationResponseDTO> listApplications(@PathVariable UUID id, @RequestAttribute("company_id") UUID companyId) {
    return this.listJobApplicationsUseCase.execute(companyId, id).stream()
        .map(JobApplicationResponseDTO::from)
        .toList();
  }
}
