package br.com.lobogang.vacancy_manager.modules.candidates;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity(name = "cadidate")  //Aqui relacionamos a entidade com a tabela do banco de dados
public class CandidateEntity {
  
  @Id // Precisa importar do Jakarta Persistence, e não do Spring Data
  @GeneratedValue(strategy = GenerationType.UUID) // Gerar o valor automaticamente, utilizando a estratégia padrão (geralmente auto-incremento)
  private UUID id;

  // @Column(name = "nome") // Utilizamos essa anotação para mapear o campo "name" para a coluna "nome" no banco de dados
  private String name;

  @NotBlank
  @Pattern(regexp = "\\S+", message = "The field [username] cannot be empty or contain only whitespace")
  private String username;
  
  @Email(message = "The field must contain a valid [email] address")
  private String email;

  @Length(min = 10, max = 100, message = "The password must be between 10 and 100 characters")
  private String password;
  private String description;
  private String curriculum;

  @CreationTimestamp
  private LocalDateTime updatedAt;


}
