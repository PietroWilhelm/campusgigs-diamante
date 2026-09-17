package br.com.fiap.campusgigs.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// Concentra num único lugar tudo relacionado a erro: as exceções de negócio
// (pequenas o suficiente para não justificar um arquivo cada) e o handler
// central que converte qualquer violação numa resposta padronizada, sem
// stack trace - conforme exigido no PDF do projeto.
@Slf4j
@RestControllerAdvice
public class ValidationHandler {

    // ---------- exceções de negócio ----------

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Violação de regra de negócio (ex.: contratar serviço inativo, contratar o próprio serviço).
    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }

    // Lançada quando a ViaCEP informa que o CEP não existe, ou quando o serviço
    // externo falha/demora e a operação não pode ficar silenciosamente incompleta.
    public static class CepNotFoundException extends RuntimeException {
        public CepNotFoundException(String message) {
            super(message);
        }
    }

    // ---------- formato padrão de resposta de erro ----------

    public record ValidationErrorResponse(String field, String message) {

        public ValidationErrorResponse(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    public record ErrorResponse(String message) {}


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationErrorResponse> handle(MethodArgumentNotValidException exception) {

        log.warn("Erro de validação: {}", exception.getMessage());

        return exception.getFieldErrors()
                .stream()
                .map(ValidationErrorResponse::new)
                .toList();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(ResourceNotFoundException exception) {
        log.warn("Recurso não encontrado: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BusinessException exception) {
        log.warn("Violação de regra de negócio: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(CepNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(CepNotFoundException exception) {
        log.warn("Erro ao consultar CEP: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(AccessDeniedException exception) {
        log.warn("Acesso negado: {}", exception.getMessage());
        return new ErrorResponse("Você não tem permissão para realizar esta operação");
    }

    // AuthenticationManager.authenticate() (chamado manualmente no AuthController)
    // lança essa exceção dentro do próprio controller - sem esse handler específico,
    // ela caía no catch-all genérico abaixo e virava 500 em vez de 401.
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handle(AuthenticationException exception) {
        log.warn("Falha de autenticação: {}", exception.getMessage());
        return new ErrorResponse("E-mail ou senha inválidos");
    }

    // Rede de segurança: qualquer exceção não mapeada acima ainda recebe uma
    // resposta centralizada e sem stack trace, em vez de cair no /error padrão.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception exception) {
        log.error("Erro inesperado", exception);
        return new ErrorResponse("Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }
}