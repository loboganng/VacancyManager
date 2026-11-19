package br.com.lobogang.vacancy_manager.modules.candidates.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import br.com.lobogang.vacancy_manager.exceptions.ApplicationAlreadyExistsException;
import br.com.lobogang.vacancy_manager.exceptions.JobNotFoundException;
import br.com.lobogang.vacancy_manager.exceptions.UserNotFoundException;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateRepository;
import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.repositories.ApplyJobRepository;
import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@ExtendWith(MockitoExtension.class)
class ApplyJobCandidateUseCaseTest {

  @InjectMocks
  private ApplyJobCandidateUseCase applyJobCandidateUseCase;

  @Mock
  private CandidateRepository candidateRepository;

  @Mock
  private JobRepository jobRepository;

  @Mock
  private ApplyJobRepository applyJobRepository;

  private final UUID candidateId = UUID.randomUUID();
  private final UUID jobId = UUID.randomUUID();

  @Test
  @DisplayName("Should not apply when the candidate does not exist")
  void shouldNotApplyWithUnknownCandidate() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> applyJobCandidateUseCase.execute(candidateId, jobId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("Should not apply to a job that does not exist")
  void shouldNotApplyToUnknownJob() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));
    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> applyJobCandidateUseCase.execute(candidateId, jobId))
        .isInstanceOf(JobNotFoundException.class);
    verify(applyJobRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should not apply twice to the same job")
  void shouldNotApplyTwice() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(new JobEntity()));
    when(applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(true);

    assertThatThrownBy(() -> applyJobCandidateUseCase.execute(candidateId, jobId))
        .isInstanceOf(ApplicationAlreadyExistsException.class);
    verify(applyJobRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should translate a unique-constraint violation into a duplicated application")
  void shouldHandleConcurrentDuplicate() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(new JobEntity()));
    // false na checagem inicial; true depois que a outra requisição gravou
    when(applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(false, true);
    when(applyJobRepository.save(any())).thenThrow(new DataIntegrityViolationException("duplicate key"));

    assertThatThrownBy(() -> applyJobCandidateUseCase.execute(candidateId, jobId))
        .isInstanceOf(ApplicationAlreadyExistsException.class);
  }

  @Test
  @DisplayName("Should not hide integrity violations that are not a duplicated application")
  void shouldRethrowOtherIntegrityViolations() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(new JobEntity()));
    when(applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(false);
    when(applyJobRepository.save(any())).thenThrow(new DataIntegrityViolationException("other"));

    assertThatThrownBy(() -> applyJobCandidateUseCase.execute(candidateId, jobId))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Should save the application linking candidate and job")
  void shouldApply() {
    when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new CandidateEntity()));
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(new JobEntity()));
    when(applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)).thenReturn(false);
    when(applyJobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    ApplyJobEntity result = applyJobCandidateUseCase.execute(candidateId, jobId);

    assertThat(result.getCandidateId()).isEqualTo(candidateId);
    assertThat(result.getJobId()).isEqualTo(jobId);
  }
}
