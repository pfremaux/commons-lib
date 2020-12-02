package commons.lib.main.console;

import commons.lib.project.documentation.MdDoc;

import java.nio.file.Path;
import java.util.List;

@MdDoc(description = "Manage a console singleton that can simulate a trivial console while running in an IDE."
+ "If you're using this factory in an IDE, please provide pre-defined inputs.")
public class ConsoleFactory {

    private static CustomConsole instance = null;

    public static CustomConsole getInstance() {
        return getInstance((Path) null);
    }

    @MdDoc(description = "Returns an implementation of a custom console.")
    public static CustomConsole getInstance(
            @MdDoc(description = "a list a predefined inputs. They will be inserted automatically while calling console.readline().")
                    List<String> answers) {
        if (instance == null) {
            if (answers != null) {
                instance = new AutomateConsole(answers);
            } else {
                instance = new RealConsole();
            }
        }
        return instance;
    }

    public static CustomConsole getInstance(Path inputFile) {
        if (instance == null) {
            if (inputFile != null && inputFile.toFile().exists()) {
                instance = new AutomateConsole(inputFile);
            } else {
                instance = new RealConsole();
            }
        }
        return instance;
    }
}
