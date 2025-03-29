package periskal.academy.backend.security.Exceptions.AuthenticationExceptions;

public class AuthenticationCommunicationException extends AuthenticationServiceException {
    public AuthenticationCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}