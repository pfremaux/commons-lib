package commons.lib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

// TODO just dropping. it's not meant to stay here
public class InMemoryDb {
    private Map<String, List<Map<String, String>>> tables = new HashMap<>();


    public int add(String tableName, Map<String, String> data) {
        final List<Map<String, String>> table = tables.get(tableName);
        table.add(data);
        return data.size() - 1;
    }

    public List<Map<String, String>> list(String table, int page, int size) {
        final List<Map<String, String>> data = tables.get(table);
        int i = page - 1;
        int lastIdx = Math.min(i * size, data.size() - 1);
        return data.subList(i, lastIdx);
    }

    public Map<String, String> get(String table, int id) {
        return tables.get(table).get(id);
    }

    public void delete(String table, int id) {
        tables.get(table).remove(id);
    }


    public static class DomainHandler {
        private final String baseRelativePath = "/domains/";
        private final InMemoryDb inMemoryDb = new InMemoryDb();

        public void handle(String method, String path, String data) {
            StringTokenizer tokenizer = new StringTokenizer(path.substring(baseRelativePath.length()), "/");
            String[] info = new String[2];
            int idxInfo = 0;
            while (tokenizer.hasMoreTokens()) {
                info[idxInfo] = tokenizer.nextToken();
                idxInfo++;
            }
            switch (method) {
                case "POST": {
                    Map<String, String> map = trivialJsonMapping(data);
                    inMemoryDb.add(info[0], map);
                    break;
                }
                case "PUT": {
                    Map<String, String> map = trivialJsonMapping(data);
                    inMemoryDb.update(info[0], Integer.parseInt(info[1]), map);
                    break;
                }
                case "GET": {
                    if (info[1] == null) {
                        List<Map<String, String>> list = inMemoryDb.list(info[0], 0, 100);
                    } else {
                        Map<String, String> stringStringMap = inMemoryDb.get(info[0], Integer.parseInt(info[1]));
                    }
                    break;
                }
            }
        }

        private Map<String, String> trivialJsonMapping(String data) {
            Map<String, String> map = new HashMap<>();
            // Yes this is trivial
            String cleanedJson = data
                    .replaceAll("\\{", "")
                    .replaceAll("\\}", "")
                    .replaceAll("\"", "")
                    ;
            StringTokenizer tokenizer1 = new StringTokenizer(cleanedJson, ",");
            while (tokenizer1.hasMoreTokens()) {
                String element = tokenizer1.nextToken();
                String[] split = element.split(":");
                map.put(split[0], split[1]);
            }
            return map;
        }
    }

    private int update(String tableName, int id, Map<String, String> map) {
        final List<Map<String, String>> table = tables.get(tableName);
        table.set(id, map);
        return id;
    }
}
