package dev.dluks.escriba.domain.exceptions.handler;

import dev.dluks.escriba.domain.exceptions.BusinessException;
import dev.dluks.escriba.domain.exceptions.DomainException;
import dev.dluks.escriba.domain.exceptions.DuplicateResourceException;
import dev.dluks.escriba.domain.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Problem> handleDomainException(DomainException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Problem problem = new Problem(
                status.value(),
                LocalDateTime.now(),
                e.getMessage(),
                null
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Problem> handleDuplicateResourceException(DuplicateResourceException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        Problem problem = new Problem(
                status.value(),
                LocalDateTime.now(),
                e.getMessage(),
                null
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Problem> handleResourceNotFoundException(ResourceNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Problem problem = new Problem(
                status.value(),
                LocalDateTime.now(),
                e.getMessage(),
                null
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Problem> handleBusinessException(BusinessException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Problem problem = new Problem(
                status.value(),
                LocalDateTime.now(),
                e.getMessage(),
                null
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(String.format("%s: %s", error.getField(), error.getDefaultMessage()))
        );

        Problem problem = new Problem(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                "Um ou mais campos estão inválidos",
                errors
        );

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Problem problem = new Problem(
                status.value(),
                LocalDateTime.now(),
                "Ocorreu um erro interno no servidor",
                null
        );

        return ResponseEntity.status(status).body(problem);
    }

    @Getter
    @AllArgsConstructor
    public static class Problem {
        private Integer status;
        private LocalDateTime timestamp;
        private String message;
        private List<String> errors;
    }
}
