package br.com.lobogang.vacancy_manager.modules.company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;

// Um candidato inscrito numa vaga, do ponto de vista da empresa.
public record JobApplicationResponseDTO(
    UUID applicationId,
    UUID candidateId,
    String name,
    String username,
    String email,
    String description,
    LocalDateTime appliedAt) {

  public static JobApplicationResponseDTO from(ApplyJobEntity application) {
    var candidate = application.getCandidateEntity();
    return new JobApplicationResponseDTO(
        application.getId(),
        application.getCandidateId(),
        candidate.getName(),
        candidate.getUsername(),
        candidate.getEmail(),
        candidate.getDescription(),
        application.getCreatedAt());
  }
}
