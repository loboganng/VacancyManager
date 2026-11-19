package br.com.lobogang.vacancy_manager.modules.candidates;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Entity(name = "candidate")  //Aqui relacionamos a entidade com a tabela do banco de dados
public class CandidateEntity {
  
  // READ_ONLY: o cliente não escolhe o id. Sem isso, um "id" no corpo do cadastro
  // faria o save() sobrescrever outro usuário.
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

  @NotBlank(message = "The field [password] is mandatory")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // a senha entra no cadastro, mas nunca sai na resposta
  @Length(min = 10, max = 100, message = "The password must be between 10 and 100 characters")
  private String password;
  private String description;
  private String curriculum;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @CreationTimestamp
  private LocalDateTime updatedAt;


}
