package commons.lib.extra.server.http;

public class HttpServerException extends Exception {
    private final String errorMessageForUser;

    public HttpServerException(String errorMessageForUser) {
        this.errorMessageForUser = errorMessageForUser;
    }

    public HttpServerException(String errorMessageForUser, String errorMessageForProgrammer) {
        super(errorMessageForProgrammer);
        this.errorMessageForUser = errorMessageForUser;
    }
}
