package commons.lib.extra.server.http;

import java.util.*;

public class Mapping {

    public static Map<String, String> trivialJsonMapping(String data) {
        Map<String, String> map = new HashMap<>();
        // Yes this is trivial
        String cleanedJson = data
                .replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll("\"", "");
        StringTokenizer tokenizer1 = new StringTokenizer(cleanedJson, ",");
        while (tokenizer1.hasMoreTokens()) {
            String element = tokenizer1.nextToken();
            String[] split = element.split(":");
            map.put(split[0].trim(), split[1].trim());
        }
        return map;
    }

    public static void fillWithJsonFormat(StringBuilder builder, Collection<String> list) {
        builder.append("[");
        for (String string : list) {
            builder.append("\"");
            builder.append(string);
            builder.append("\"");
            builder.append(",");
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
    }

    public static void fillWithJsonFormat(StringBuilder builder, List<Map<String, String>> list) {
        builder.append("[");
        for (Map<String, String> stringStringMap : list) {
            fillWithJsonFormat(builder, stringStringMap);
            builder.append(",");
        }
        if (list.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
    }

    public static void fillWithJsonFormat(StringBuilder builder, Map<String, String> stringStringMap) {
        builder.append("{");
        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
            builder.append("\"");
            builder.append(entry.getKey());
            builder.append("\":\"");
            builder.append(entry.getValue());
            builder.append("\"");
            builder.append(",");
        }
        if (!stringStringMap.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
    }
}
