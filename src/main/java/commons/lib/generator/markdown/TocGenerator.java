package commons.lib.generator.markdown;

import commons.lib.FileUtils;
import commons.lib.documentation.Documentation;
import commons.lib.documentation.MdDoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@MdDoc(description = "Generate a table of content (markdown format).")
public class TocGenerator {

    private final Path baseDocumentationDir;

    public TocGenerator(Path baseDocumentationDir) throws FileNotFoundException {
        if (!FileUtils.isDirectoryAndExist(baseDocumentationDir)) {
            throw new FileNotFoundException(
                    String.format("The provided baseDirectory doesn't exist : %s", baseDocumentationDir.toFile().getAbsolutePath())
            );
        }
        this.baseDocumentationDir = baseDocumentationDir;
    }

    public static void main(String[] args) throws IOException {
        Path baseDir = Paths.get("C:\\Users\\Pierre\\IdeaProjects\\seriousOne\\password-store\\commons-lib\\src\\main\\doc");
        TocGenerator tocGenerator = new TocGenerator(baseDir);
        Documentation toc = tocGenerator.run();
        MarkdownGenerator markdownGenerator = new MarkdownGenerator(baseDir);
        markdownGenerator.generate(toc, "toc.md");
    }

    private Documentation run() throws IOException {
        final Documentation toc = new Documentation();
        final List<File> files = Files.find(baseDocumentationDir,
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .map(Path::toFile)
                .filter(f -> f.getName().endsWith(".md"))
                .collect(Collectors.toList());
        toc.title(1, "Table of content");
        for (File file : files) {
            // TODO make a relative link to github
            toc.pinPoint(file.getName(), false);
            toc.linkDoc(file.getName());
        }
        toc.separator();
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        toc.italic("Generated : " + datePattern.format(LocalDateTime.now()));
        return toc;
    }


}
