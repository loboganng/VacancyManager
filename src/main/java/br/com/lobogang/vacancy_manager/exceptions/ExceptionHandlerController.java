package br.com.lobogang.vacancy_manager.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

  // Criando a interface MessaSource para poder pegar as mensagens de erro do arquivo messages.properties
  private MessageSource messageSource;

  // Precisamos inicializar o MessageSource para poder usar ele no método de tratamento de exceção
  // E para que o Spring não inicialize como null, precisamos criar um construtor para injetar o MessageSource
  public ExceptionHandlerController(MessageSource message) {
    this.messageSource = message;
  }

  // Trata os erros de validação dos campos (@Valid): devolve uma lista com a mensagem e o campo que causou o erro.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    List<ErrorMessageDTO> dto = new ArrayList<>();

    e.getBindingResult().getFieldErrors().forEach(err -> {
      String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());
      dto.add(new ErrorMessageDTO(message, err.getField()));
    });

    return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
  }

  // Erros de regra de negócio: cada exceção vira um status HTTP, sem try/catch nos controllers.
  @ExceptionHandler({ UserFoundException.class, ApplicationAlreadyExistsException.class })
  public ResponseEntity<ErrorMessageDTO> handleConflict(RuntimeException e) {
    return error(HttpStatus.CONFLICT, e);
  }

  @ExceptionHandler({ UserNotFoundException.class, JobNotFoundException.class })
  public ResponseEntity<ErrorMessageDTO> handleNotFound(RuntimeException e) {
    return error(HttpStatus.NOT_FOUND, e);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorMessageDTO> handleInvalidCredentials(InvalidCredentialsException e) {
    return error(HttpStatus.UNAUTHORIZED, e);
  }

  private ResponseEntity<ErrorMessageDTO> error(HttpStatus status, RuntimeException e) {
    return ResponseEntity.status(status).body(new ErrorMessageDTO(e.getMessage(), null));
  }
}
