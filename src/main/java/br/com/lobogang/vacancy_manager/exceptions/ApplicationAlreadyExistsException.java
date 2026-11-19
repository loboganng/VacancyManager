package br.com.lobogang.vacancy_manager.exceptions;

public class ApplicationAlreadyExistsException extends RuntimeException {
  public ApplicationAlreadyExistsException() {
    super("Candidate has already applied to this job");
  }
}
