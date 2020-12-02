package commons.lib.main.filestructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represent a CSV like file structure.
 */
public final class StructuredFile {

    private static final Logger logger = LoggerFactory.getLogger(StructuredFile.class);

    private final String separator;
    private List<String> currentLine;
    private final List<List<String>> fileData;

    /**
     * @param separator The separator between each datum. (In CSV file it would be ';')
     */
    public StructuredFile(String separator) {
        this(separator, new ArrayList<>());
    }

    private StructuredFile(String separator, List<List<String>> fileData) {
        this.separator = separator;
        this.fileData = fileData;
        currentLine = new ArrayList<>();
    }

    public void newLine() {
        fileData.add(currentLine);
        logger.debug("Going to a new line (number {})", fileData.size());
        currentLine = new ArrayList<>();
    }

    public void add(String column) {
        logger.trace("Adding column {}", column);
        if (column.contains(separator)) {
            throw new IllegalArgumentException("Separator character detected in a column. Change the separator or change the column data. Separator = " + separator);
        }
        currentLine.add(column);
    }


    /**
     * Generates a structured file from the provided data.
     *
     * @param data      The bytes that should be structured as expected by this class.
     * @param separator The separator between each datum (In CSV file it would be ';').
     * @param nbrFields Maximum number of field expected in a row.
     * @return A new instance of StructuredFile with the provided data
     */
    public static StructuredFile load(byte[] data, String separator, int nbrFields) {
        String fileStr = new String(data, StandardCharsets.UTF_8);
        logger.info("data to load has {} bytes", data.length);
        logger.info("data = {}", fileStr);
        String[] split = fileStr.split("\n");
        logger.info("data to load has {} lines", split.length);
        List<List<String>> structuredData = new ArrayList<>();
        for (String s : split) {
            logger.info("processing : -{}-", s);
            final String cleanLine = s.trim();
            if (cleanLine.length() == 0) {
                logger.info("empty line, next !");
                continue;
            }
            List<String> strings = cutLine(cleanLine, separator, nbrFields);
            structuredData.add(strings);
        }
        return new StructuredFile(separator, structuredData);
    }

    /**
     * Splits a line depending on its separator. It ensures the number of fields is correct. Otherwise
     * empty strings are added. Extra fields will be added with no error.
     *
     * @param strLine      The line
     * @param separator    The separator between each datum.
     * @param maxNbrFields The max number of fields in this line
     * @return The cut line.
     */
    private static List<String> cutLine(String strLine, String separator, int maxNbrFields) {
        logger.info("Cutting line {} with separator {}", strLine, separator);
        String[] split = strLine.split(separator);
        logger.info("Number of token for the current line : ", split.length);
        final List<String> lines = new ArrayList<>(Arrays.asList(split));
        for (int i = lines.size(); i < maxNbrFields; i++) {
            lines.add("");
        }
        return lines;
    }

    private static String stringifyLine(List<String> line, String separator) {
        final StringBuilder sb = new StringBuilder();
        for (String column : line) {
            sb.append(column);
            sb.append(separator);
        }
        sb.delete(sb.length() - separator.length(), sb.length());
        return sb.toString();
    }

    private String stringifyAll() {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<String> fileDatum : getFileData()) {
            String stringifyLine = stringifyLine(fileDatum, separator);
            logger.info("new stringified line : {}", stringifyLine);
            stringBuilder.append(stringifyLine);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public byte[] toByteArray() {
        final String wholeFile = stringifyAll();
        logger.info("Whole file : \n{}", wholeFile);
        return wholeFile.getBytes(StandardCharsets.UTF_8);
    }

    public List<List<String>> getFileData() {
        return Collections.unmodifiableList(fileData);
    }

    @Override
    public String toString() {
        return this.fileData.toString();
    }
}
