package br.com.lobogang.vacancy_manager.exceptions;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Username/password invalid");
  }
}
