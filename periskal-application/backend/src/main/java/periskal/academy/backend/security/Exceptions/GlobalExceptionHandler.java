package periskal.academy.backend.security.exceptions;


import lombok.extern.slf4j.Slf4j;
import periskal.academy.backend.dto.ErrorResponse;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationCommunicationException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationInvalidCredentialsException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationParsingException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationServerErrorException;
import periskal.academy.backend.security.exceptions.authentication.AuthenticationServiceException;
import periskal.academy.backend.security.exceptions.general.ResourceNotFoundException;
import periskal.academy.backend.security.exceptions.general.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice 
@Slf4j
public class GlobalExceptionHandler {

    
    //#region Authetnication Exceptions

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
        String detail = "External service failed with status " + ex.getStatusCode();
        log.error("Authentication failed: Server error from external service. Status: {}, Body: {}",
                  ex.getStatusCode(), ex.getResponseBody(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_GATEWAY, 
                "Authentication service reported an internal error.",
                detail 
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
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

    @ExceptionHandler(AuthenticationInvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(AuthenticationInvalidCredentialsException ex, WebRequest request) {
        log.info("Invalid credentials attempt: {}", ex.getMessage()); 
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED, // 401 Unauthorized 
                "Authentication failed. Please check your username and password.", 
                "Invalid credentials provided." 
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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

    //#endregion


    //#region General Exceptions

    @ExceptionHandler(ResourceNotFoundException.class) 
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(RuntimeException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage()); 
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The requested resource was not found.", // General user message
                ex.getMessage() // Specific exception message as details
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    // Handle custom validation exceptions
    @ExceptionHandler(ValidationException.class) 
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(), 
                ex.getDetails()   
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



}