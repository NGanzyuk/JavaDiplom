package projects.javadiplom.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestExceptionHandler {
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<?> requestException(RequestException exception,HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(new RequestException(exception.getMessage()));
    }
}
