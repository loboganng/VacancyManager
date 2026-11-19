package br.com.lobogang.vacancy_manager.modules.company.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.exceptions.UserNotFoundException;
import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.CompanyRepository;
import br.com.lobogang.vacancy_manager.modules.company.repositories.JobRepository;

@Service
public class CreateJobUseCase {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private CompanyRepository companyRepository;

  public JobEntity execute(JobEntity jobEntity) {
    // O token pode ser válido e a empresa já não existir: conferimos antes de salvar
    var company = this.companyRepository.findById(jobEntity.getCompanyId())
        .orElseThrow(UserNotFoundException::new);

    var saved = this.jobRepository.save(jobEntity);
    saved.setCompanyEntity(company);
    return saved;
  }
}
