package commons.lib.main.web;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HtmlTagFactory {

    public static HtmlTag title(HtmlTag parent, String text, String classStyle) {
        final HtmlTag h1 = new HtmlTag(parent, "h1");
        h1.getAttributes().put("class", classStyle);
        return h1;
    }

    public static void main(String[] args) {
        HtmlTag root = new HtmlTag("body");
        TableCreator.create(root)
        .titlesRow("nom", "prenom")
        .data(Arrays.asList(
                List.of("f", "p")
        ));
    }

    public static class HtmlTag {
        private final HtmlTag parent;
        private final String tagName;
        private final Map<String, String> attributes = new HashMap<>();

        public HtmlTag(HtmlTag parent, String tr) {
            this.parent = parent;
            this.tagName = tr;
        }

        public HtmlTag(String tagName) {
            this(null, tagName);
        }

        public HtmlTag getParent() {
            return parent;
        }

        public String getTagName() {
            return tagName;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }
    }


    public static class TableCreator {
        private final HtmlTag tableTag;
        private int columnNumber = 1;
        private Function<Object, String>[] transformers;

        private TableCreator(HtmlTag parent) {
            this.tableTag = new HtmlTag(parent, "table");
        }

        public static TableCreator create(HtmlTag parent) {
            return new TableCreator(parent);
        }

        public TableCreator titlesRow(String... titles) {
            columnNumber = titles.length;
            final HtmlTag tr = new HtmlTag(tableTag, "tr");
            for (String title : titles) {
                new HtmlTag(tr, title);
            }
            return this;
        }

        public TableCreator cellCreator(Function<Object, String>... transformers) {
            this.transformers = transformers;
            return this;
        }

        public <T> TableCreator data(List<List<Object>> rows) {
            for (List row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    Object column = row.get(i);
                    String cellData = transformers[i].apply(column);
                    HtmlTag tr = new HtmlTag(tableTag, "tr");
                    HtmlTag td = new HtmlTag(tr, "td");
                    HtmlTag textTag = new HtmlTag(td, "fullText");
                    textTag.getAttributes().put("text", cellData);
                }
            }
            return this;
        }
    }

}

