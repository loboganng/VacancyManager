package br.com.lobogang.vacancy_manager.modules.company.useCases;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.exceptions.JobNotFoundException;
import br.com.lobogang.vacancy_manager.modules.candidates.entities.ApplyJobEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.repositories.ApplyJobRepository;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@Service
public class ListJobApplicationsUseCase {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private ApplyJobRepository applyJobRepository;

  public List<ApplyJobEntity> execute(UUID companyId, UUID jobId) {
    // Vaga de outra empresa responde 404, igual a vaga inexistente:
    // assim a API não revela que aquele id existe.
    var job = this.jobRepository.findById(jobId)
        .filter(found -> found.getCompanyId().equals(companyId))
        .orElseThrow(JobNotFoundException::new);

    return this.applyJobRepository.findByJobIdOrderByCreatedAtAsc(job.getId());
  }
}
