package commons.lib.main.filestructure.graphviz.model;

import java.util.Objects;

public class GVNode {
    private final String id;
    private final String label;
    private final String shape;
    private final GVColor color;
    private final GVStyleText styleText;
    private final String image;
    private final GVLabelLoc labelLoc;



    public GVNode(String id, String label, String shape, GVColor color, GVStyleText styleText, String image, GVLabelLoc labelLoc) {
        this.id = id;
        this.label = label;
        this.shape = shape;
        this.color = color;
        this.styleText = styleText;
        this.image = image;
        this.labelLoc = labelLoc;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getShape() {
        return shape;
    }

    public GVColor getColor() {
        return color;
    }

    public GVStyleText getStyleText() {
        return styleText;
    }

    public String getImage() {
        return image;
    }

    public StringBuilder toDotFormat() {
        Objects.requireNonNull(id);
        Objects.requireNonNull(label);
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(" [label=").append(label);
        if (styleText != null) {
            builder.append(", style").append(styleText.toString().toLowerCase());
        }
        if (shape != null) {
            builder.append(", shape").append(shape.toLowerCase());
        }
        if (labelLoc != null){
            builder.append(", labelloc=").append(labelLoc);
        }

        builder.append("];\n");
        return builder;
    }
}
