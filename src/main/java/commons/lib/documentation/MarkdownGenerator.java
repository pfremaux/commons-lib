package commons.lib.documentation;

import commons.lib.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.stream.IntStream;

public class MarkdownGenerator {


    private final Path baseDocumentationDir;

    public MarkdownGenerator(Path baseDocumentationDir) throws FileNotFoundException {
        if (!FileUtils.isDirectoryAndExist(baseDocumentationDir)) {
            throw new FileNotFoundException(
                    String.format("The provided baseDirectory doesn't exist : %s", baseDocumentationDir.toFile().getAbsolutePath())
            );
        }
        this.baseDocumentationDir = baseDocumentationDir;
    }

    public void generate(Documentation documentation, String fileName) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final Stack<String> openedTag = new Stack<>();
        for (Documentation.Fragment fragment : documentation.getFragments()) {
            IntStream.range(0, fragment.getTitleLevel()).forEach(i -> builder.append("#"));
            if (fragment.isPinPoint()) {
                builder.append("- ");
            }
            if (fragment.getNumPoint() != null) {
                builder.append(fragment.getNumPoint());
                builder.append(". ");
            }
            if (fragment.isBold()) {
                builder.append("**");
                openedTag.push("**");
            }
            if (fragment.isItalic()) {
                builder.append("*");
                openedTag.push("*");
            }
            if (fragment.isCode()) {
                builder.append("`");
                openedTag.push("`");
            }
            if (fragment.isQuote()) {
                builder.append("> ");
            }
            if (fragment.getExternalLink() != null) {
                builder.append("[");
                builder.append(fragment.getExternalLink());
                builder.append("]");
                builder.append("(");
                builder.append(fragment.getExternalLink());
                builder.append(")");
            }
            if (fragment.getMdRelativePath() != null) {
                builder.append("[");
                builder.append(fragment.getMdRelativePath());
                builder.append("]");
                builder.append("(");
                builder.append(fragment.getMdRelativePath());
                builder.append(")");
            }
            builder.append(fragment.getContent());
            closeAllTags(builder, openedTag);
            if (fragment.isNewLine()) {
                builder.append("\n");
            } else {
                builder.append(" ");
            }
            if (fragment.isEndWithHorizontalRule()) {
                builder.append("\n---\n");
            }
        }
        String fileContent = builder.toString();
        Files.writeString(baseDocumentationDir.resolve(fileName), fileContent);
    }

    private void closeAllTags(StringBuilder builder, Stack<String> openedTag) {
        while (!openedTag.isEmpty()) {
            builder.append(openedTag.pop());
        }
    }

    public Path getBaseDocumentationDir() {
        return baseDocumentationDir;
    }
}
