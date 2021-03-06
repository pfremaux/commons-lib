package commons.lib.main.os;

import commons.lib.main.console.v3.AppInfo;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.*;

public class LogUtils {
    private static final String DEFAULT_FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
    private static Logger logger;

    private LogUtils() {

    }

    public static Logger initLogs() {
        AppInfo appInfo = new AppInfo();
        if (appInfo.isInIde()) {
            return initIdeLogs(appInfo);
        } else {
            return initLogsForBinaryApp(appInfo);
        }
    }

    private static Logger initIdeLogs(AppInfo appInfo) {
        if (logger == null) {
            logger = Logger.getLogger(appInfo.getAppName());
            ConsoleHandler handler = new ConsoleHandler();

            SimpleFormatter newFormatter = new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format(DEFAULT_FORMAT,
                            Instant.now().toEpochMilli(),
                            record.getLevel().getLocalizedName(),
                            record.getMessage()
                    );
                }
            };

            handler.setFormatter(newFormatter);
            handler.setLevel(Level.FINE);
            logger.addHandler(handler);
        }

        return logger;
    }

    private static Logger initLogsForBinaryApp(AppInfo appInfo) {
        if (logger == null) {
            logger = Logger.getLogger(appInfo.getAppName());
            FileHandler fh = null;
            try {
                fh = new FileHandler(appInfo.getAppName() + "-logs.log", 100, 10);
                fh.setFormatter(new SimpleFormatter());
                fh.setLevel(Level.FINE);
                logger.addHandler(fh);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logger;
    }

}
