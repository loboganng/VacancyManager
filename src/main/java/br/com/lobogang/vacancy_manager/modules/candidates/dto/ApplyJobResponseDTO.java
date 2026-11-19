package br.com.lobogang.vacancy_manager.modules.candidates.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;

public record ApplyJobResponseDTO(UUID id, UUID candidateId, UUID jobId, LocalDateTime createdAt) {

  public static ApplyJobResponseDTO from(ApplyJobEntity application) {
    return new ApplyJobResponseDTO(
        application.getId(),
        application.getCandidateId(),
        application.getJobId(),
        application.getCreatedAt());
  }
}
