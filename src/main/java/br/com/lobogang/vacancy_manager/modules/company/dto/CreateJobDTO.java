package br.com.lobogang.vacancy_manager.modules.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobDTO {

  @NotBlank(message = "The field [description] is mandatory")
  private String description;

  private String benefits;

  @NotBlank(message = "The field [level] is mandatory")
  private String level;

}
