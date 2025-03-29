package periskal.academy.backend.security.Exceptions;


import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ErrorResponse;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationCommunicationException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationParsingException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationServerErrorException;
import periskal.academy.backend.security.Exceptions.AuthenticationExceptions.AuthenticationServiceException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest; 

@ControllerAdvice 
@Slf4j
public class GlobalExceptionHandler {

    
    @ExceptionHandler(AuthenticationCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleCommunicationException(AuthenticationCommunicationException ex, WebRequest request) {
        log.error("Authentication failed: Communication error with external service.", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE, // 503 Service Unavailable - Good for temporary network issues
                "Authentication service is currently unavailable. Please try again later.",
                ex.getMessage() 
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AuthenticationServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleServerErrorException(AuthenticationServerErrorException ex, WebRequest request) {
        log.error("Authentication failed: Server error from external service. Status: {}, Body: {}",
                  ex.getStatusCode(), ex.getResponseBody(), ex);

        HttpStatus status = HttpStatus.BAD_GATEWAY; // 502 Bad Gateway is often suitable when a downstream server fails
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "Authentication service reported an internal error. Please contact support if the problem persists.",
                "External service failed with status " + ex.getStatusCode() 
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(AuthenticationParsingException.class)
    public ResponseEntity<ErrorResponse> handleParsingException(AuthenticationParsingException ex, WebRequest request) {
        log.error("Authentication failed: Could not parse response from external service.", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, // 500 - Our service failed to process a valid-looking response
                "Failed to process the response from the authentication service.",
                ex.getMessage() 
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // This is a catch-all for any other unexpected exceptions that might occur during authentication
    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleBaseAuthException(AuthenticationServiceException ex, WebRequest request) {
        log.error("Authentication failed: Unexpected authentication service error.", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred during authentication.",
                ex.getMessage() 
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}