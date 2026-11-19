package br.com.lobogang.vacancy_manager.modules.candidates.useCases;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.exceptions.ApplicationAlreadyExistsException;
import br.com.lobogang.vacancy_manager.exceptions.JobNotFoundException;
import br.com.lobogang.vacancy_manager.exceptions.UserNotFoundException;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateRepository;
import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.repositories.ApplyJobRepository;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@Service
public class ApplyJobCandidateUseCase {

  @Autowired
  private CandidateRepository candidateRepository;

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private ApplyJobRepository applyJobRepository;

  public ApplyJobEntity execute(UUID candidateId, UUID jobId) {
    this.candidateRepository.findById(candidateId).orElseThrow(UserNotFoundException::new);
    this.jobRepository.findById(jobId).orElseThrow(JobNotFoundException::new);

    if (this.applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)) {
      throw new ApplicationAlreadyExistsException();
    }

    var application = ApplyJobEntity.builder()
        .candidateId(candidateId)
        .jobId(jobId)
        .build();

    try {
      return this.applyJobRepository.save(application);
    } catch (DataIntegrityViolationException e) {
      // Duas requisições ao mesmo tempo podem passar pela checagem acima;
      // a constraint única do banco barra a segunda. Qualquer outra violação sobe como está.
      if (this.applyJobRepository.existsByCandidateIdAndJobId(candidateId, jobId)) {
        throw new ApplicationAlreadyExistsException();
      }
      throw e;
    }
  }
}
