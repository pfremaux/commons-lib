package commons.lib.os;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandLineExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineExecutor.class);
    private Mutable<Map<String, CommandStatus>> executedCommands = new MutableObject<>(new HashMap<>());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        CommandStatus dir = commandLineExecutor.execute("cmd /c \"dir %userprofile% /s\"");
        commandLineExecutor.waitAllCommands(20);
        System.out.println(dir.getLogs().toString());
        System.out.println("> End of the program");
        System.exit(0);
    }

    public void waitAllCommands(long sleepInMs) {
        boolean allFinished = false;
        while (!allFinished) {
            allFinished = true;
            for (Map.Entry<String, CommandStatus> entry : executedCommands.getValue().entrySet()) {
                allFinished &= entry.getValue().getExitCode() != null;
            }
            try {
                Thread.sleep(sleepInMs);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public CommandStatus waitFastestCommand(long sleepInMs) {
        while (true) {
            for (Map.Entry<String, CommandStatus> entry : executedCommands.getValue().entrySet()) {
                if (entry.getValue().getExitCode() != null) {
                    return entry.getValue();
                }
            }
            try {
                Thread.sleep(sleepInMs);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void execute(String... commands) {
        for (String command : commands) {
            execute(command);
        }
    }

    public CommandStatus execute(String command) {
        final Runtime runtime = Runtime.getRuntime();
        final CommandStatus commandStatus = new CommandStatus();
        executedCommands.getValue().put(commandStatus.getCommandId(), commandStatus);
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
                //exec.waitFor();
                commandStatus.setExitCode(exec.exitValue());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-3);
            }
            commandStatus.setExitCode(0);
            return "test";
        });
        //String s = submit.get();
        //System.out.println("=== " + s);
        /*while (!executorService.isTerminated()) {
            System.err.println("not termine");
        }*/
        //executorService.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("termination passé ");
        return commandStatus;
    }

    public CommandStatus getCommandStatus(String uuid) {
        return executedCommands.getValue().get(uuid);
    }

}
