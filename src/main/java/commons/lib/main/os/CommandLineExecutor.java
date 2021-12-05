package commons.lib.main.os;

import commons.lib.main.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class CommandLineExecutor {
    private static final Logger logger = LogUtils.initLogs();
    private Map<String, CommandStatus> executedCommands = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        // CommandStatus dir = commandLineExecutor.execute("cmd /c \"dir %userprofile% /s\"");
        CommandStatus dir = commandLineExecutor.execute("cmd /c \"dir %userprofile%\"");
        commandLineExecutor.waitAllCommands(2);
        System.out.println(dir.getLogs().toString());
        System.out.println("> End of the program");
        System.exit(0);
    }

    public void waitAllCommands(long sleepInMs) {
        boolean allFinished = false;
        while (!allFinished) {
            allFinished = true;
            for (Map.Entry<String, CommandStatus> entry : executedCommands.entrySet()) {
                allFinished &= entry.getValue().getExitCode() != null;
            }
            try {
                Thread.sleep(sleepInMs);
            } catch (InterruptedException e) {
                logger.throwing(this.getClass().toString(), e.getMessage(), e);
            }
        }
    }

    public CommandStatus waitFastestCommand(long sleepInMs) {
        while (true) {
            for (Map.Entry<String, CommandStatus> entry : executedCommands.entrySet()) {
                if (entry.getValue().getExitCode() != null) {
                    return entry.getValue();
                }
            }
            try {
                Thread.sleep(sleepInMs);
            } catch (InterruptedException e) {
                logger.throwing(this.getClass().toString(), e.getMessage(), e);
            }
        }
    }

    public void execute(String... commands) throws ExecutionException, InterruptedException {
        for (String command : commands) {
            execute(command);
        }
    }

    public CommandStatus execute(String command) throws InterruptedException, ExecutionException {
        final Runtime runtime = Runtime.getRuntime();
        final CommandStatus commandStatus = new CommandStatus();
        executedCommands.put(commandStatus.getCommandId(), commandStatus);
        Future<String> submit = executorService.submit(() -> {
            try {
                final Process exec = runtime.exec(command);
                final InputStream inputStream = exec.getInputStream();
                final InputStreamReader bufferedInputStream = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(bufferedInputStream);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    commandStatus.getLogs().append(line).append("\n");
                }
                //exec.waitFor();
                commandStatus.setExitCode(exec.exitValue());
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-3);
            }
            commandStatus.setExitCode(0);
            return "test";
        });
        String s = submit.get();
        //System.out.println("=== waiting " + s);
        /*while (!executorService.isTerminated()) {
            System.err.println("not termine");
            Thread.sleep(2000);
        }*/
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("termination passé ");
        return commandStatus;
    }

    public CommandStatus getCommandStatus(String uuid) {
        return executedCommands.get(uuid);
    }

    public static void validateEndOfExecution(CommandStatus commandStatus) {
        if (commandStatus.getExitCode() == null || commandStatus.getExitCode() != 0) {
            System.out.println(commandStatus.getLogs().toString());
            System.err.println("Failed to download project source.");
            SystemUtils.failSystem();
        }
    }

}
