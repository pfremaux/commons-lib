package commons.lib.main;

import commons.lib.project.documentation.MdDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

@MdDoc(description = "Utility tools for file management.")
public final class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {

    }

    @MdDoc(description = "Read a whole file.")
    public static String readFile(@MdDoc(description = "The path of the file to read") Path path) throws IOException {
        return Files.readString(path);
    }

    @MdDoc(description = "Test if the provided path is a directory.")
    public static boolean isDirectoryAndExist(@MdDoc(description = "The string path to test.") String path) {
        return isDirectoryAndExist(Paths.get(path));
    }

    @MdDoc(description = "Test if the provided path is a directory.")
    public static boolean isDirectoryAndExist(
            @MdDoc(description = "The string path to test.") Path path) {
        return Files.isDirectory(path);
    }

    public static boolean isFileExist(Path path) {
        return Files.isRegularFile(path);
    }

    @MdDoc(description = "Create a directory. It doesn't create missing directories.")
    public static boolean createDirectory(
            @MdDoc(description = "The string path of the new directory") String path) {
        return createDirectory(Paths.get(path));
    }

    @MdDoc(description = "Create recursively all missing directory in the provided path" +
            " and returns the number of directory created")
    public static int recursiveCreateDirectory(
            @MdDoc(description = "The base path that must already exist") Path basePath,
            @MdDoc(description = "The directory path that has to exist") String strPath) {
        final Path path = Paths.get(strPath);
        final Stack<Path> pathStack = new Stack<>();
        // Add the initial path as the last directory to create
        Path currentPath = path;
        pathStack.push(currentPath);
        // Repeat until getParent() is the baseDir
        while(!currentPath.equals(basePath)) {
            currentPath = currentPath.getParent();
            pathStack.push(currentPath);
        }
        // Remove the null value
        pathStack.pop();
        int nbrCreation = 0;
        while (!pathStack.isEmpty()) {
            final Path pop = pathStack.pop();
            if (pop == null || pop.equals(basePath)) {
                break;
            }
            if (!pop.toFile().exists()) {
                createDirectory(pop);
                nbrCreation++;
            }
        }
        return nbrCreation;
    }

    public static boolean createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn(e.getMessage(), e);
            return false;
        }
        return true;
    }

}
