package be16.ordersystem.common.service;

import be16.ordersystem.common.dto.CommonErrorDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationError(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        String errMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), errMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        String errMessage = "잘못된 형식의 요청입니다.";
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), errMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new CommonErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
