package br.com.lobogang.vacancy_manager.modules.candidates;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface CandidateRepository extends JpaRepository<CandidateEntity, UUID> {
  
  // Agora criaremos um padrão para que o Spring não permita criação de usuários com o mesmo email ou username
  Optional<CandidateEntity> findByUsernameOrEmail(String username, String email);
}
