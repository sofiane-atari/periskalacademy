package periskal.academy.backend.dto;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; 
    private String message; 


    public ErrorResponse(HttpStatus httpStatus, String message, String details) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;      
    }

}