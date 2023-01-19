package commons.lib.main.filestructure.graphviz.model;

public enum GVLabelLoc {
    ROOT("b"),
    NODE("c"),
    CLUSTER("t");

    String id;
    GVLabelLoc(String t) {
        this.id = t;
    }

    public String getId() {
        return id;
    }
}

