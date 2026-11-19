package br.com.lobogang.vacancy_manager.modules.candidates.useCases;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import br.com.lobogang.vacancy_manager.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.modules.candidates.CandidateRepository;
import br.com.lobogang.vacancy_manager.modules.candidates.dto.ProfileCandidateResponseDTO;

@Service
public class ProfileCandidateUseCase {

  @Autowired
  private CandidateRepository candidateRepository;
  
  public ProfileCandidateResponseDTO execute(UUID idCandidate) {
    var candidate =this.candidateRepository.findById(idCandidate).orElseThrow(UserNotFoundException::new);

    var candidateDTO = ProfileCandidateResponseDTO.builder().description(candidate.getDescription()).username(candidate.getUsername()).email(candidate.getEmail()).name(candidate.getName()).id(candidate.getId()).build();

    return candidateDTO;
  }
}
