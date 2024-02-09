package projects.javadiplom.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class RequestException extends Throwable {
    String message;
    public RequestException(String message) {
        this.message =  message;
    }

    public String getMessage() {
        return message;
    }
}
