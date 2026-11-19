package br.com.lobogang.vacancy_manager.modules.company.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lobogang.vacancy_manager.exceptions.JobNotFoundException;
import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.repositories.ApplyJobRepository;
import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@ExtendWith(MockitoExtension.class)
class ListJobApplicationsUseCaseTest {

  @InjectMocks
  private ListJobApplicationsUseCase listJobApplicationsUseCase;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private ApplyJobRepository applyJobRepository;

  private final UUID companyId = UUID.randomUUID();
  private final UUID jobId = UUID.randomUUID();

  @Test
  @DisplayName("Should hide applications of a job that belongs to another company")
  void shouldNotListApplicationsOfAnotherCompany() {
    var job = JobEntity.builder().id(jobId).companyId(UUID.randomUUID()).build();
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    assertThatThrownBy(() -> listJobApplicationsUseCase.execute(companyId, jobId))
        .isInstanceOf(JobNotFoundException.class);
    verify(applyJobRepository, never()).findByJobIdOrderByCreatedAtAsc(any());
  }

  @Test
  @DisplayName("Should list the applications of the company's own job")
  void shouldListApplications() {
    var job = JobEntity.builder().id(jobId).companyId(companyId).build();
    var application = ApplyJobEntity.builder().jobId(jobId).candidateId(UUID.randomUUID()).build();
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(applyJobRepository.findByJobIdOrderByCreatedAtAsc(jobId)).thenReturn(List.of(application));

    assertThat(listJobApplicationsUseCase.execute(companyId, jobId)).containsExactly(application);
  }
}
