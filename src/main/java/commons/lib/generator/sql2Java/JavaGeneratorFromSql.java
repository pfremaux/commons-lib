package commons.lib.generator.sql2Java;

import commons.lib.UnrecoverableException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates java file from SQL scripts. This class will only work with SQLite.
 * <p>
 * This link might be useful to start...
 *
 * @see <a href="https://sqlitebrowser.org/dl/">Sqlite browser</a> allows you to create/edit a SQLite DB.
 */
public final class JavaGeneratorFromSql {
    private final Path dbDirectory;
    private final Path[] sqlScripts;
    private final Path javaProjectFullPath;
    private final String parentPackages;

    public static void main(String[] args) throws IOException {
        JavaGeneratorFromSql sql = new JavaGeneratorFromSql(
                Paths.get("."), // path where the DB is. Used for the jdbc path in order to open de DB.
                Paths.get("C:\\Users\\Account\\IdeaProjects\\home-lib\\src\\main\\java\\home\\tools\\lib\\generator\\classifier"),// path where to generate the classes
                "commons.lib.generator.classifier", // java package path
                Paths.get("C:\\Users\\Account\\Downloads\\SQLiteDatabaseBrowserPortable\\Data\\repo.db.sql") // script path to analyze
        );
        sql.compute();
    }

    /**
     * @param dbDirectory         Directory where the DB will be read while executing the generated source code.
     * @param javaProjectFullPath The full path where you want to generate the Java files. Including the java packages.
     * @param parentPackages      Packages where the code will be generated.
     * @param sqlScripts          An array of all the sql scripts that contains the DB structure.
     */
    public JavaGeneratorFromSql(Path dbDirectory, Path javaProjectFullPath, String parentPackages, Path... sqlScripts) {
        this.dbDirectory = dbDirectory;
        this.javaProjectFullPath = javaProjectFullPath;
        this.parentPackages = parentPackages;
        this.sqlScripts = sqlScripts;
    }

    /**
     * Process the sql scripts in order to generate the corresponding java files.
     *
     * @throws IOException e.
     */
    private void compute() throws IOException {
        for (Path sqlScript : sqlScripts) {
            final String nameAndExtension = sqlScript.toFile().getName();
            String inputType = InputParameter.DB_TYPE.getPropertyString();
            if (inputType.equalsIgnoreCase("sqlite")) {
                final String destinationScriptForJdbcUri = getSqliteFileUri(dbDirectory, nameAndExtension);
                DaoGenerator.manage(javaProjectFullPath.toFile().getAbsolutePath(), parentPackages, sqlScript, destinationScriptForJdbcUri);
            } else {
                String message = "DB type not supported : " + inputType;
                throw new UnrecoverableException(message, message, -1);
            }
        }
    }

    /**
     * Generates the JDBC URI (jdbc:sqlite:<path>).
     * This method is currently made for local sql files.
     *
     * @param pathDbDirectory       Path to the directory.
     * @param fileNameWithExtension Just the file name with its extension.
     * @return The JDBC URI (jdbc:sqlite:<path>).
     */
    private static String getSqliteFileUri(Path pathDbDirectory, String fileNameWithExtension) {
        return "jdbc:sqlite:" + pathDbDirectory.toFile().getAbsolutePath().replaceAll("\\\\", "/") + "/" + fileNameWithExtension;
    }

}
