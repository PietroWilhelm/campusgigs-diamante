package br.com.fiap.campusgigs.validation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

// Concentra num único lugar tudo relacionado a erro: as exceções de negócio
// (pequenas o suficiente para não justificar um arquivo cada) e o handler
// central que converte qualquer violação numa resposta padronizada, sem
// stack trace - conforme exigido no PDF do projeto.
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

    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            List<FieldError> fields
    ) {
        public record FieldError(String field, String message) {}

        public static ErrorResponse of(int status, String error, String message) {
            return new ErrorResponse(LocalDateTime.now(), status, error, message, null);
        }

        public static ErrorResponse ofValidation(int status, String error, List<FieldError> fields) {
            return new ErrorResponse(LocalDateTime.now(), status, error, "Erro de validação", fields);
        }
    }

    // ---------- handlers ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ErrorResponse body = ErrorResponse.ofValidation(HttpStatus.BAD_REQUEST.value(), "Bad Request", fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse body = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({BusinessException.class, CepNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBusiness(RuntimeException ex) {
        ErrorResponse body = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse body = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "Forbidden",
                "Você não tem permissão para realizar esta operação");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        ErrorResponse body = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                "Credenciais ausentes, inválidas ou token expirado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
