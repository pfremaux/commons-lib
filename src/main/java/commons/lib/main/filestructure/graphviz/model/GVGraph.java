package commons.lib.main.filestructure.graphviz.model;

import java.util.*;

public class GVGraph {
    public static final String DEFAULT_FONT = "Helvetica,Arial,sans-serif";
    private final String name;
    private final String fontName;
    private final String nodeFontStyle;
    private final String edgeFontStyle;
    private final Map<String, GVNode> idToNode;
    private final List<GVEdge> edges;


    private GVGraph(String name, String fontName, String nodeFontStyle, String edgeFontStyle, Map<String, GVNode> idToNode, List<GVEdge> edges) {
        this.name = name;
        this.fontName = fontName;
        this.nodeFontStyle = nodeFontStyle;
        this.edgeFontStyle = edgeFontStyle;
        this.idToNode = Collections.unmodifiableMap(idToNode);
        this.edges = Collections.unmodifiableList(edges);
    }

    public StringBuilder toDotFormat() {
        final StringBuilder builder = new StringBuilder();
        builder.append("graph ").append(name).append(" {\n");
        builder.append("fontname=\"").append(fontName).append("\"\n");
        builder.append("node [fontname=\"").append(nodeFontStyle).append("\"]\n");
        builder.append("edge [fontname=\"").append(edgeFontStyle).append("\"]\n");
        for (GVNode gvNode : idToNode.values()) {
            builder.append(gvNode.toDotFormat());
        }
        for (GVEdge gvEdge : edges) {
            builder.append(gvEdge.totoDotFormat());
        }
        builder.append("}");
        return builder;
    }

    public static class Builder {
        private String name;
        private String fontName = DEFAULT_FONT;
        private String nodeFontStyle = DEFAULT_FONT;
        private String edgeFontStyle = DEFAULT_FONT;
        private Map<String, GVNode> idToNode = new HashMap<>();
        private List<GVEdge> edges = new ArrayList<>();

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return self();
        }

        public Builder setFontName(String fontName) {
            this.fontName = fontName;
            return self();
        }

        public Builder setNodeFontStyle(String nodeFontStyle) {
            this.nodeFontStyle = nodeFontStyle;
            return self();
        }

        public Builder setEdgeFontStyle(String edgeFontStyle) {
            this.edgeFontStyle = edgeFontStyle;
            return self();
        }

        public Builder withNode(GVNode node) {
            idToNode.put(node.getId(), node);
            return self();
        }

        public Builder withEdge(GVEdge edge) {
            edges.add(edge);
            return self();
        }


        public GVGraph build() {
            return new GVGraph(name, fontName, nodeFontStyle, edgeFontStyle, idToNode, edges);
        }

        public Builder self() {
            return this;
        }
    }
}
