package periskal.academy.backend.security.Exceptions.authentication;

public class AuthenticationCommunicationException extends AuthenticationServiceException {
    public AuthenticationCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}