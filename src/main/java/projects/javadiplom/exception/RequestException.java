package projects.javadiplom.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RequestException {
    public ResponseEntity<?> getMessage(String token, HttpStatus httpStatus) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("auth-token", token);
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        StringBuilder body = new StringBuilder();
        return new ResponseEntity(body.toString(), responseHeaders, httpStatus);
    }
}
