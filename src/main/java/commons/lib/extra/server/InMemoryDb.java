package commons.lib.extra.server;

import commons.lib.main.StringUtils;
import commons.lib.main.filestructure.StructuredFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO just dropping. it's not meant to stay here
public class InMemoryDb {
    private Map<String, List<Map<String, String>>> tables = new HashMap<>();


    public int add(String tableName, Map<String, String> data) {
        final List<Map<String, String>> table = tables.computeIfAbsent(tableName, s -> new ArrayList<>());
        final String idField = StringUtils.capitalize(false, "id", tableName);
        data.put(idField, Integer.toString(table.size() + 1));
        table.add(data);
        return table.size();
    }

    public List<Map<String, String>> list(String table, int page, int size) {
        final List<Map<String, String>> data = tables.computeIfAbsent(table, s -> new ArrayList<>());
        int i = page - 1;
        int lastIdx = Math.min(page * size, data.size());
        final List<Map<String, String>> pageData = data.subList(i, lastIdx);
        int id = i * size;
        Iterator<Map<String, String>> iterator = pageData.iterator();
        String capitalize = StringUtils.capitalize(true, table);
        int counter = 1;
        while (iterator.hasNext()) {
            final Map<String, String> next = iterator.next();
            next.put("id" + capitalize, Integer.toString(id + counter));
            counter++;
        }
        return pageData;
    }

    public Map<String, String> get(String table, int id) {
        return tables.get(table).get(id);
    }

    public void delete(String table, int id) {
        tables.get(table).remove(id);
    }

    public int update(String tableName, int id, Map<String, String> map) {
        final List<Map<String, String>> table = tables.get(tableName);
        table.set(id, map);
        return id;
    }

    public void save(Path path) throws IOException {
        for (Map.Entry<String, List<Map<String, String>>> entry : tables.entrySet()) {
            final String table = entry.getKey();
            final StructuredFile structuredFile = new StructuredFile(";");
            for (Map<String, String> row : entry.getValue()) {
                for (Map.Entry<String, String> column : row.entrySet()) {
                    structuredFile.add(column.getKey());
                    structuredFile.add(column.getValue());
                }
                structuredFile.newLine();
            }
            Files.write(path.resolve(table + ".csv"), structuredFile.toByteArray());
        }
    }

    public void loadAll(Path path) throws IOException {
        final List<Path> files;
        try (Stream<Path> walk = Files.walk(path)) {
            files = walk.filter(path1 -> path1.toFile().getName().endsWith(".csv")).collect(Collectors.toList());
        }
        for (Path file : files) {
            final StructuredFile load = StructuredFile.load(Files.readAllBytes(file), ";", 0);
            final String absolutePath = file.toFile().getAbsolutePath();
            final int lastSlash = absolutePath.lastIndexOf(File.separator);
            final String tableName = absolutePath.substring(lastSlash + 1, absolutePath.length() - ".csv".length());
            final List<Map<String, String>> tableData = new ArrayList<>();
            for (List<String> datum : load.getFileData()) {
                final Map<String, String> row = new HashMap<>();
                for (int i = 0; i < datum.size(); i += 2) {
                    String columnName = datum.get(i);
                    String value = datum.get(i + 1);
                    row.put(columnName, value);
                }
                tableData.add(row);
            }
            tables.computeIfAbsent(tableName, k -> new ArrayList<>()).addAll(tableData);
        }
    }

    public Set<String> tables() {
        return tables.keySet();
    }

    public void clearAll() {
        tables.clear();
    }
}
