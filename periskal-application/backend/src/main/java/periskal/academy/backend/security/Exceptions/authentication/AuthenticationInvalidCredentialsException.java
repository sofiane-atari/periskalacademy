package periskal.academy.backend.security.Exceptions.authentication;

public class AuthenticationInvalidCredentialsException extends AuthenticationServiceException {

    public AuthenticationInvalidCredentialsException(String message) {
        super(message);
    }

}