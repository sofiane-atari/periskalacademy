package periskal.academy.backend.security.Exceptions.authentication;

public class AuthenticationParsingException extends AuthenticationServiceException {
    private final String responseXml;

    public AuthenticationParsingException(String message, Throwable cause, String responseXml) {
        super(message, cause);
        this.responseXml = responseXml;
    }

    public String getResponseXml() {
        return responseXml;
    }
}
