package periskal.academy.backend.security.exceptions.authentication;

public class AuthenticationServerErrorException extends AuthenticationServiceException {
    private final int statusCode;
    private final String responseBody;

    public AuthenticationServerErrorException(String message, int statusCode, String responseBody) {
        super(message + " - Status: " + statusCode + ", Body: " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}