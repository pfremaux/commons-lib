package commons.lib.main.filestructure.graphviz.model;

public class GVEdge {

    private final GVNode from;
    private final GVNode to;
    private final GVColor color;
    private final GVStyleText styleText;

    public GVEdge(GVNode from, GVNode to, GVColor color, GVStyleText styleText) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.styleText = styleText;
    }

    public GVNode getFrom() {
        return from;
    }

    public GVNode getTo() {
        return to;
    }

    public GVColor getColor() {
        return color;
    }

    public GVStyleText getStyleText() {
        return styleText;
    }

    public StringBuilder totoDotFormat() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" ").append(from.getId()).append(" -- ").append(to.getId()).append("  ").append("[");
        if (styleText != null) {
            builder.append("style=").append(styleText).append(",");
        }
        if (color != null) {
            builder.append("color=").append(",");
        }
        if (builder.lastIndexOf(",") == builder.length()) {
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("];\n");
        return builder;
    }
}
