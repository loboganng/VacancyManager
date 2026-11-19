package br.com.lobogang.vacancy_manager.modules.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;

// O que a API devolve sobre uma vaga. Não expõe a entidade da empresa inteira.
public record JobResponseDTO(
    UUID id,
    String description,
    String benefits,
    String level,
    UUID companyId,
    String companyName,
    LocalDateTime createdAt) {

  public static JobResponseDTO from(JobEntity job) {
    var company = job.getCompanyEntity();
    return new JobResponseDTO(
        job.getId(),
        job.getDescription(),
        job.getBenefits(),
        job.getLevel(),
        job.getCompanyId(),
        company != null ? company.getName() : null,
        job.getCreatedAt());
  }
}
