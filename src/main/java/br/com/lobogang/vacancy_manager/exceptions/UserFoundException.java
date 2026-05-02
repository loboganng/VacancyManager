package br.com.lobogang.vacancy_manager.exceptions;

public class UserFoundException extends RuntimeException{
  public UserFoundException(){
    super("User already exists");
  }
}
