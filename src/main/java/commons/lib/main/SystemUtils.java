package commons.lib.main;

public final class SystemUtils {

    public static final int EXIT_SUCCESSFUL = 0;
    public static final int EXIT_USER_MISTAKE = -4;
    public static final int EXIT_PROGRAMMER_ERROR = -5;
    public static final int EXIT_SYSTEM_ERROR = -6;

    private SystemUtils() {

    }

    public static void failUser() {
        System.exit(EXIT_USER_MISTAKE);
    }

    public static void failProgrammer() {
        System.exit(EXIT_PROGRAMMER_ERROR);
    }

    public static void failSystem() {
        System.exit(EXIT_SYSTEM_ERROR);
    }

    public static void endOfApp() {
        System.exit(EXIT_SUCCESSFUL);
    }
}
