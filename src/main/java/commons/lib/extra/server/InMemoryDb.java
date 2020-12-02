package commons.lib.extra.server;

import commons.lib.main.StringUtils;

import java.util.*;

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
        return data.subList(i, lastIdx);
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

    public Set<String> tables() {
        return tables.keySet();
    }
}
