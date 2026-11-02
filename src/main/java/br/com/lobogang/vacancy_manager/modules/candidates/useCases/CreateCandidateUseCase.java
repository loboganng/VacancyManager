package br.com.lobogang.vacancy_manager.modules.candidates.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.lobogang.vacancy_manager.exceptions.UserFoundException;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateEntity;
import br.com.lobogang.vacancy_manager.modules.candidates.CandidateRepository;

@Service
public class CreateCandidateUseCase {
  
  @Autowired
  private CandidateRepository candidateRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public CandidateEntity execute(CandidateEntity candidateEntity){
    // Aqui ficará a lógica de criação de um candidato, como validação de dados, verificação de duplicidade, etc.

      // Verificamos se já existe um usuário com o mesmo username ou email, para evitar duplicidade
    this.candidateRepository.
                          findByUsernameOrEmail
                            (candidateEntity.getUsername(), candidateEntity.getEmail())
                          .ifPresent((user) -> {
                            throw new UserFoundException();
                          });

    var password = passwordEncoder.encode(candidateEntity.getPassword());
    candidateEntity.setPassword(password);

    // Se não houver um usuário, salvamos ele no banco de dados
    return this.candidateRepository.save(candidateEntity);
  }
}
