package commons.lib.main;

public final class RecoverableException extends RuntimeException {

    private final String[] messageInformUser;
    private final int recoveryId;

    public RecoverableException(String message) {
        this(message, new String[]{message}, -1);
    }

    public RecoverableException(String message, String messageInformUser, int recoveryId) {
        this(message, new String[]{messageInformUser}, recoveryId);
    }

    public RecoverableException(String message, String[] messageInformUser, int recoveryId) {
        super(message);
        this.messageInformUser = messageInformUser;
        this.recoveryId = recoveryId;
    }

    public RecoverableException(String message, String messageInformUser, Throwable cause, int recoveryId) {
        this(message, new String[]{messageInformUser}, cause, recoveryId);
    }

    public RecoverableException(String message, String[] messageInformUser, Throwable cause, int recoveryId) {
        super(message, cause);
        this.messageInformUser = messageInformUser;// + "Exiting with error " + exitCode;
        this.recoveryId = recoveryId;
    }

    public String[] getMessageInformUser() {
        return messageInformUser;
    }

    public int getRecoveryId() {
        return recoveryId;
    }
}
