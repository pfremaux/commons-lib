package commons.lib.functionaltests.entrypoint;

import commons.lib.functionaltests.settings.FunctionalTestsSettings;
import commons.lib.functionaltests.socket.secured.SecuredChat;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.os.LogUtils;
import java.util.logging.Logger;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FT_SecuredChat {
    private static final Logger logger = LogUtils.initLogs();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        LogUtils.debug("Starting functional test");
        run();
        // TODO assert files
        LogUtils.debug("Ending functional test");
    }

    private static void run() {
        executorService.submit(() -> SecuredChat.main(new String[]{getInputFileClient1()}));
        executorService.submit(() -> SecuredChat.main(new String[]{getInputFileClient2()}));
        try {
            executorService.awaitTermination(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            CustomConsole instance = ConsoleFactory.getInstance();
            System.out.println(instance.history());
        }
    }

    private static String getInputFileClient1() {
        return System.getProperty(FunctionalTestsSettings.MAIN_INPUT_DIR_PROP) + System.getProperty(FunctionalTestsSettings.SECURED_CHAT_INPUT_FILE_CLIENT_1_PROP);
    }


    private static String getInputFileClient2() {
        return System.getProperty(FunctionalTestsSettings.MAIN_INPUT_DIR_PROP) + System.getProperty(FunctionalTestsSettings.SECURED_CHAT_INPUT_FILE_CLIENT_2_PROP);
    }
}
