package br.com.lobogang.vacancy_manager.modules.candidates.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import br.com.lobogang.vacancy_manager.modules.candidates.CandidateEntity;
import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Candidatura: liga um candidato a uma vaga. A constraint única impede, no banco,
// que o mesmo candidato se candidate duas vezes à mesma vaga.
@Entity
@Table(name = "apply_jobs", uniqueConstraints = @UniqueConstraint(columnNames = { "candidate_id", "job_id" }))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyJobEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
  private CandidateEntity candidateEntity;

  @Column(name = "candidate_id", nullable = false)
  private UUID candidateId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", insertable = false, updatable = false)
  private JobEntity jobEntity;

  @Column(name = "job_id", nullable = false)
  private UUID jobId;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
