package commons.lib.functionaltests.entrypoint;

import commons.lib.functionaltests.console.v3.AskName;
import commons.lib.functionaltests.console.v3.DisplayName;
import commons.lib.functionaltests.console.v3.FakeSubSection;
import commons.lib.functionaltests.settings.FunctionalTestsSettings;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;

import java.nio.file.Path;

public class FT_ConsoleV3 {

    public static void main(String[] args) {
        ConsoleRunner consoleRunner = new ConsoleRunner("default", new ConsoleItem[]{
                new AskName(),
                new DisplayName(),
                new FakeSubSection()
        });
        ConsoleFactory.getInstance(Path.of(getInputConsole()));
        consoleRunner.run();
        System.out.println(ConsoleFactory.getInstance().history());
        System.out.println(ConsoleFactory.getInstance().getOutputWhileDebugging());
        assert ConsoleFactory.getInstance().getOutputWhileDebugging().get(0).equals("1. Set name");
    }

    private static String getInputConsole() {
        return System.getProperty(FunctionalTestsSettings.MAIN_INPUT_DIR_PROP) + System.getProperty(FunctionalTestsSettings.CONSOLE_V3_INPUT_FILE);
    }

}
