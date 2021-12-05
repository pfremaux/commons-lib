package commons.lib.main.os;

import java.io.File;
import java.util.*;

public class FileListingUtils {

    static Map<String, List<File>> db = new HashMap<>();

    public static File[] listFiles(File baseDir, List<String> extensions) {
        return baseDir.listFiles((dir, name) -> new File(dir + "/" + name).isDirectory() || extensions.contains(name.substring(name.lastIndexOf('.') + 1)));
    }

    public static List<File> recursiveListFiles(File baseDir, List<String> extensions, List<String> ignoreDirectoryName, List<String> ignoreExtensions) {
        final File[] files = listFiles(baseDir, extensions);
        final List<File> result = new ArrayList<>();
        mainLoop:
        for (File file : files) {
            if (file.isDirectory()) {
                if (ignoreDirectoryName.contains(file.getName())) {
                    continue;
                }
                List<File> subResult = recursiveListFiles(file, extensions, ignoreDirectoryName, ignoreExtensions);
                result.addAll(subResult);
            } else {
                for (String ignoreExtension : ignoreExtensions) {
                    if (file.getName().endsWith(ignoreExtension)) {
                        System.out.println("ignore : " + file.getName());
                        continue mainLoop;
                    }
                }
                result.add(file);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<File> exes = recursiveListFiles(new File("E:\\HTTP SERVER\\miniweb"), Arrays.asList("webm", "mp4", "jpg", "jpeg", "png"), Collections.emptyList(), Collections.emptyList());
        for (File file : exes) {
            db.computeIfAbsent(file.getParent(), k -> new ArrayList<>()).add(file);
        }
        System.out.println(db.size());
    }
}
