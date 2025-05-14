package periskal.academy.backend.security.exceptions.authentication;

public class AuthenticationCommunicationException extends AuthenticationServiceException {
    public AuthenticationCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}