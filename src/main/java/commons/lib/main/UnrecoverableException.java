package commons.lib.main;

public final class UnrecoverableException extends RuntimeException {

    private final String[] messageInformUser;
    private final int exitCode;

    public UnrecoverableException(String message, String messageInformUser, int exitCode) {
        this(message, new String[]{messageInformUser}, exitCode);
    }

    public UnrecoverableException(String message, String[] messageInformUser, int exitCode) {
        super(message);
        this.messageInformUser = messageInformUser;
        this.exitCode = exitCode;
    }

    public UnrecoverableException(String message, String messageInformUser, Throwable cause, int exitCode) {
        this(message, new String[]{messageInformUser}, cause, exitCode);
    }

    public UnrecoverableException(String message, String[] messageInformUser, Throwable cause, int exitCode) {
        super(message, cause);
        this.messageInformUser = messageInformUser;// + "Exiting with error " + exitCode;
        this.exitCode = exitCode;
    }

    public String[] getMessageInformUser() {
        return messageInformUser;
    }

    public int getExitCode() {
        return exitCode;
    }
}
