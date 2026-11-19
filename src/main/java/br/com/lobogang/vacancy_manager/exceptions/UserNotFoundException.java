package br.com.lobogang.vacancy_manager.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {
    super("User not found");
  }
}
