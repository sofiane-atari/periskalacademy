package periskal.academy.backend.security.Exceptions.AuthenticationExceptions;

public class AuthenticationInvalidCredentialsException extends AuthenticationServiceException {

    public AuthenticationInvalidCredentialsException(String message) {
        super(message);
    }

}