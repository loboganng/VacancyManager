package br.com.lobogang.vacancy_manager.modules.candidates.useCases;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@Service
public class ListJobsByFilterUseCase {

  @Autowired
  private JobRepository jobRepository;

  // Filtro vazio ou ausente devolve todas as vagas
  public List<JobEntity> execute(String filter) {
    var term = filter == null ? "" : filter.trim();
    return this.jobRepository.findByDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(term);
  }
}
