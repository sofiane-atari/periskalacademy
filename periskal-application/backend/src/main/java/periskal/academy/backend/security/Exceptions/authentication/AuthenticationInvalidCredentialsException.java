package periskal.academy.backend.security.exceptions.authentication;

public class AuthenticationInvalidCredentialsException extends AuthenticationServiceException {

    public AuthenticationInvalidCredentialsException(String message) {
        super(message);
    }

}