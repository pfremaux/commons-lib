package commons.lib.main.filestructure.mapping;

import java.util.List;

public class PartialPropertyName {

    private final boolean isRoot;
    private final String name;
    private final boolean isArray;
    private final List<PartialPropertyName> attributes;

    public PartialPropertyName(String name, List<PartialPropertyName> attributes, boolean isArray) {
        this.isRoot = false;
        this.name = name;
        this.isArray = isArray;
        this.attributes = attributes;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public String getName() {
        return name;
    }

    public List<PartialPropertyName> getAttributes() {
        return attributes;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public String toString() {
        return "PartialPropertyName{" +
                "isRoot=" + isRoot +
                ", name='" + name + '\'' +
                ", isArray=" + isArray +
                ", attributes=" + attributes +
                '}';
    }
}
