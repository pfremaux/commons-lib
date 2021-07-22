package commons.lib.tooling.sql2Java;


import commons.lib.main.FileUtils;
import commons.lib.main.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Generates the DAO (Data Access Objects) depending on the provided script.
 */
final class DaoGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DaoGenerator.class);

    /**
     * @param basePath                    The full path where you want to generate the Java files.
     * @param basePack                    The full java package name where you want to generate the Java files.
     * @param sqlFile                     SQL file path that will be processed.
     * @param destinationScriptForJdbcUri The JDBC URI that will be inserted in the Java code in order to connect to the DB.
     * @throws IOException e.
     */
    static void manage(String basePath, String basePack, Path sqlFile, String destinationScriptForJdbcUri) throws IOException {
        final String sqlData = FileUtils.readFile(sqlFile);
        manage(basePath, basePack, sqlData, destinationScriptForJdbcUri);
    }

    private static void manage(String basePath, String basePack, String creation, String destinationScriptForJdbcUri) throws IOException {
        final String cleanedRequest = creation
                .replaceAll("\n", " ")
                .replaceAll("\r", " ")
                .replaceAll("\t", " ")
                .replaceAll("  ", " ")
                .replaceAll(",", " ,");
        logger.debug("Parsing... " + cleanedRequest);
        final StringTokenizer tokenizer = new StringTokenizer(cleanedRequest, " ");
        String expect = null;
        String tableName = "";
        String columnName = "";
        ColumnType columnType;
        List<Column> columns = new ArrayList<>();
        final Map<String, List<Column>> tablesDefinition = new HashMap<>();
        while (tokenizer.hasMoreElements()) {
            String s = tokenizer.nextToken();
            logger.debug("Current word : " + s);
            if (Arrays.asList("create", "table").contains(s.toLowerCase())) {
                expect = "TABLE_NAME";
            } else if ("TABLE_NAME".equals(expect)) {
                tableName = StringUtils.removeQuotes(s);
                expect = "table(";
            } else if ("(".equals(s) && "table(".equals(expect)) {
                expect = "COLUMN_NAME";
            } else if ("COLUMN_NAME".equals(expect)) {
                if (Arrays.asList("PRIMARY", "FOREIGN").contains(s)) {
                    expect = ",colOr)";
                } else {
                    columnName = StringUtils.capitalize(s.replaceAll("\"", ""), false);
                    expect = "COLUMN_TYPE";
                }
            } else if ("COLUMN_TYPE".equals(expect)) {
                columnType = ColumnType.fromString(s);
                columns.add(new Column(columnName, columnType));
                expect = ",colOr)";
            } else if (",colOr)".equalsIgnoreCase(expect) &&
                    (",".equals(s) || s.contains(")"))) {
                if (",".equals(s)) {
                    expect = "COLUMN_NAME";
                } else {
                    logger.debug("Saving in memory table " + tableName);
                    tablesDefinition.put(tableName, columns);
                    tableName = "";
                    columns = new ArrayList<>();
                    expect = "CREATE TABLE";
                }
            } else {
                logger.debug(String.format("Found '%s'", s));
            }
            logger.debug("Next word expected : " + expect);
        }
        logger.debug("Generating Java files...");
        for (Map.Entry<String, List<Column>> entry : tablesDefinition.entrySet()) {
            logger.debug("Processing table : " + entry.getKey());
            createPojo(basePath, basePack, entry.getKey(), entry.getValue());
            createDao(basePath, basePack, entry.getKey(), entry.getValue(), destinationScriptForJdbcUri);
        }
        logger.debug("Java file generation finished.");
    }

    private static void createDao(String basePath, String basePack, String tableName, List<Column> columns, String jdbcUri) throws IOException {
        final Path base = Paths.get(basePath);
        final Path pathDao = base.resolve("dao");
        if (!FileUtils.isDirectoryAndExist(pathDao)) {
            FileUtils.createDirectory(pathDao);
        }
        final String capClass = StringUtils.capitalize(tableName, true);
        final Path javaFile = pathDao.resolve(capClass + "Dao.java");
        final List<String> lines = new ArrayList<>();
        lines.add("package " + basePack + ".dao;");
        lines.add("");
        lines.add("import java.sql.*;");
        lines.add("import " + basePack + ".pojo." + capClass + ";");
        lines.add("import java.util.Optional;");
        lines.add("");
        lines.add("public final class " + capClass + "Dao {");
        lines.add("\tprivate static final String URI = \"" + jdbcUri + "\";");
        lines.add("\n");

        StringBuilder sb = getInsertMethod(tableName, columns, capClass);
        lines.add(sb.toString());

        sb = getSelectMethod(tableName, columns, capClass);
        lines.add(sb.toString());

        sb = getGetInstance(tableName, columns);
        lines.add(sb.toString());

        lines.add(getConnectMethod().toString());

        lines.add("}");
        logger.debug("Generating file... " + javaFile);
        Files.write(javaFile, lines);
    }

    private static StringBuilder getSelectMethod(String tableName, List<Column> columns, String capClass) {
        StringBuilder sb = methodSignature(Collections.singletonList("int id"),
                "public", "Optional<" + capClass + ">", "select");

        sb.append(" throws SQLException {\n");
        sb.append("\t\tfinal Connection conn = connect();\n");
        sb.append("\t\ttry (PreparedStatement ps = conn.prepareStatement(\"select ");
        for (Column column : columns) {
            sb.append(StringUtils.snakeCase(column.getName()));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ");
        sb.append(tableName);
        sb.append(" where id=?;\")) {\n");

        sb.append("\t\t\tps.setInt(1, id);\n");
        sb.append("\t\t\ttry (ResultSet resultSet = ps.executeQuery()) {\n");
        sb.append("\t\t\t\tif (resultSet.next()) {\n");
        sb.append("\t\t\t\t\treturn get");
        sb.append(capClass);
        sb.append("Instance(resultSet);\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t}\n");
        sb.append("\t\tconn.close();\n");
        sb.append("\t\treturn Optional.empty();\n");
        sb.append("\t}");

        return sb;
    }

    private static StringBuilder getGetInstance(String tableName, List<Column> columns) {
        final String className = StringUtils.capitalize(tableName, true);
        StringBuilder sb = methodSignature(List.of("ResultSet rs"), "public", String.format("Optional<%s>", className), "get" + className + "Instance");
        sb.append(" throws SQLException {\n");
        sb.append("\t\treturn Optional.of(new ");
        sb.append(className);
        sb.append("(");
        for (Column column : columns) {
            sb.append("rs.get");
            sb.append(column.getColumnType().getJavaSqlType());
            sb.append("(\"");
            sb.append(StringUtils.snakeCase(column.getName()));
            sb.append("\"),");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("));\n");
        sb.append("\t}\n");
        return sb;
    }

    private static StringBuilder getConnectMethod() {
        StringBuilder sb = methodSignature(Collections.emptyList(), "public", "Connection", "connect");
        sb.append(" {\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\treturn DriverManager.getConnection(URI);\n");
        sb.append("\t\t} catch (SQLException e) {\n");
        sb.append("\t\t\tthrow new RuntimeException(e);\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        return sb;
    }

    private static StringBuilder getInsertMethod(String tableName, List<Column> columns, String capClass) {
        StringBuilder sb = methodSignature(Collections.singletonList(parameter(capClass)),
                "public", "int", "insert");

        sb.append(" throws SQLException {\n");
        sb.append("\t\tfinal Connection conn = connect();\n");
        sb.append("\t\tfinal int generatedId;\n");
        sb.append("\t\t\ttry (PreparedStatement ps = conn.prepareStatement(\"insert into ");
        sb.append(tableName);
        sb.append(" (");
        for (Column column : columns) {
            if (!column.getName().equalsIgnoreCase("id")) {
                sb.append(StringUtils.snakeCase(column.getName()));
                sb.append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") values(");
        for (Column column : columns) {
            if (!column.getName().equalsIgnoreCase("id")) {
                sb.append("?,");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");\", Statement.RETURN_GENERATED_KEYS)) {\n");

        String entity = StringUtils.capitalize(capClass, false);
        int counter = 1;
        for (Column column : columns) {
            if (!column.getName().equalsIgnoreCase("id")) {
                sb.append("\t\t\t\tps.set");
                sb.append(column.columnType.getJavaSqlType());
                sb.append("(");
                sb.append(counter++);
                sb.append(", ");
                sb.append(entity);
                sb.append(".get");
                sb.append(StringUtils.capitalize(column.getName(), true));
                sb.append("());\n");
            }
        }
        sb.append("\t\t\t\tps.execute();\n");
        sb.append("\t\t\t\tfinal ResultSet generatedKeys = ps.getGeneratedKeys();\n");
        sb.append("\t\t\t\tif (generatedKeys.next()) {\n");
        sb.append("\t\t\t\t\tgeneratedId = generatedKeys.getInt(1);\n");
        sb.append("\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\tgeneratedId = -1;\n");
        sb.append("\t\t\t\t}\n");

        sb.append("\t\t\t}\n");
        sb.append("\t\tconn.close();\n");
        sb.append("\t\treturn generatedId;\n");
        sb.append("\t}");
        return sb;
    }

    private static String parameter(String className) {
        return className + " " + StringUtils.capitalize(className, false);
    }

    private static StringBuilder methodSignature(List<String> parameters, String... starting) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        for (String word : starting) {
            sb.append(word);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("(");
        if (!parameters.isEmpty()) {
            for (String parameter : parameters) {
                sb.append(parameter);
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb;
    }

    private static void createPojo(String basePath, String basePack, String tableName, List<Column> columns) throws IOException {
        final Path base = Paths.get(basePath);
        final Path pathPojo = base.resolve("pojo");
        if (!FileUtils.isDirectoryAndExist(pathPojo)) {
            FileUtils.createDirectory(pathPojo);
        }
        final String capClass = StringUtils.capitalize(tableName, true);
        final Path javaFile = pathPojo.resolve(StringUtils.removeQuotes(capClass) + ".java");
        final List<String> lines = new ArrayList<>();
        lines.add("package " + basePack + ".pojo;");
        lines.add("");
        lines.add("public final class " + capClass + " {");
        lines.add("");
        List<String> parametersConstructor = new ArrayList<>();
        for (Column column : columns) {
            String typeAndName = column.getColumnType().getJavaType() + " " + column.getName();
            parametersConstructor.add(typeAndName);
            lines.add("\tprivate final " + typeAndName + ";");
        }
        lines.add("");
        lines.add("\tpublic " + capClass + "(" + String.join(", ", parametersConstructor) + ") {");
        for (Column c : columns) {
            lines.add("\t\tthis." + c.getName() + " = " + c.getName() + ";");
        }
        lines.add("\t}");
        lines.add("");
        for (Column column : columns) {
            lines.add("\tpublic " + column.getColumnType().getJavaType() + " get" + StringUtils.capitalize(column.getName(), true) + "() {");
            lines.add("\t\treturn this." + column.getName() + ";");
            lines.add("\t}");
            lines.add("");
        }
        lines.add("}");
        logger.debug("Generating file... " + javaFile);
        Files.write(javaFile, lines);
    }

    public static class Column {
        private final String name;
        private final ColumnType columnType;

        Column(String name, ColumnType columnType) {
            this.name = name;
            this.columnType = columnType;
        }

        public String getName() {
            return name;
        }

        ColumnType getColumnType() {
            return columnType;
        }
    }
}
