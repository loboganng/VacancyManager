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
  public ExceptionHandlerController(MessageSource message){
    this.messageSource = message;
  }

// Função para tratar as exceções de validação dos campos, ou seja, quando o usuário enviar um campo vazio ou com um formato inválido, o Spring vai lançar uma exceção do tipo MethodArgumentNotValidException, e esse método vai tratar essa exceção e retornar uma lista de mensagens de erro para o usuário, onde cada mensagem de erro vai conter a mensagem de erro e o nome do campo que causou o erro.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
    
    // Criando um array para armazenar as mensagens de erro, onde cada mensagem de erro vai conter a mensagem de erro e o nome do campo que causou o erro.
    List<ErrorMessageDTO> dto = new ArrayList<>();
    
    // Função para percorrer a lista de erros de validação dos campos, onde cada erro de validação vai conter o nome do campo que causou o erro e a mensagem de erro, e vamos adicionar essa mensagem de erro na lista de mensagens de erro que criamos anteriormente.
    e.getBindingResult().getFieldErrors().forEach(err -> {
      String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());
      ErrorMessageDTO error = new ErrorMessageDTO(message, err.getField());
      dto.add(error);
    });
    
    // Retornamos uma ResponseEntity com a lista de mensagens de erro e o status HTTP 400 (Bad Request)
    return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
  }
  
}
