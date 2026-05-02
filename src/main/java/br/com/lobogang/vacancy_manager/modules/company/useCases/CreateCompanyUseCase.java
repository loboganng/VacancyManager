package br.com.lobogang.vacancy_manager.modules.company.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.exceptions.UserFoundException;
import br.com.lobogang.vacancy_manager.modules.company.entities.CompanyEntity;
import br.com.lobogang.vacancy_manager.modules.company.repositories.CompanyRepository;


@Service
public class CreateCompanyUseCase {


  @Autowired
  private CompanyRepository companyRepository;

  public CompanyEntity execute(CompanyEntity companyEntity){

    this.companyRepository.findByUsernameOrEmail(
                            companyEntity.getUsername(), 
                            companyEntity.getEmail())
                            .ifPresent((user) -> {
                              throw new UserFoundException();
                            });;

    return this.companyRepository.save(companyEntity);
  }
}
