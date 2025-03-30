package periskal.academy.backend.dto;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; 
    private String message; 
    private String details; 
    private List<String> validationErrors;


    // Constructor for general errors with a single detail string
    public ErrorResponse(HttpStatus httpStatus, String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.details = details;
        this.validationErrors = null; 
    }

    // Constructor for validation errors with multiple details
    public ErrorResponse(HttpStatus httpStatus, String message, List<String> validationErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.details = null; 
        this.validationErrors = validationErrors;
    }

     // Constructor for errors with no specific details field needed
     public ErrorResponse(HttpStatus httpStatus, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.details = null;
        this.validationErrors = null;
    }
}