package br.com.lobogang.vacancy_manager.modules.candidates.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;

public interface ApplyJobRepository extends JpaRepository<ApplyJobEntity, UUID> {

  boolean existsByCandidateIdAndJobId(UUID candidateId, UUID jobId);

  List<ApplyJobEntity> findByJobIdOrderByCreatedAtAsc(UUID jobId);
}
