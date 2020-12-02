package commons.lib.main.os;

import java.util.UUID;

public class CommandStatus {
    private final String commandId;
    private final StringBuffer logs = new StringBuffer();
    private Integer exitCode = null;

    public CommandStatus() {
        this.commandId = UUID.randomUUID().toString();
    }

    public StringBuffer getLogs() {
        return logs;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public String getCommandId() {
        return commandId;
    }
}
