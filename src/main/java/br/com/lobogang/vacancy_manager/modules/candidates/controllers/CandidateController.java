package br.com.lobogang.vacancy_manager.modules.candidates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lobogang.vacancy_manager.modules.candidates.CandidateEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidate")

public class CandidateController {

  @Autowired  //Permite que o Spring Boot injete automaticamente uma instância do CandidateRepository na classe CandidateController, para que possamos usar seus métodos para interagir com o banco de dados.
  private CandidateRepository candidateRepository;

  @PostMapping("/")
  public CandidateEntity create(@Valid @RequestBody CandidateEntity candidateEntity){
    return this.candidateRepository.save(candidateEntity);

  }
}