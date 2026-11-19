package br.com.lobogang.vacancy_manager.modules.company.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lobogang.vacancy_manager.modules.company.entities.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, UUID> {

  // Vagas da empresa logada, mais recentes primeiro
  List<JobEntity> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);

  // Busca do candidato: "Containing" vira LIKE %filtro% e "IgnoreCase" ignora maiúsculas/minúsculas
  List<JobEntity> findByDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(String filter);
}
