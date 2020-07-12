package commons.lib.filestructure.freemind;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplifiedNode {
    private final String value;
    private final List<SimplifiedNode> children = new ArrayList<>();

    public SimplifiedNode(String value) {
        this.value = value;
    }

    public static SimplifiedNode node(String s) {
        return new SimplifiedNode(s);
    }

    public SimplifiedNode with(SimplifiedNode... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public FreemindNode getFreemindInstance() {
        final FreemindNode freemindNode = new FreemindNode(
                "ID_" + TestFreemind.RANDOM.longs(100000000L, 9999999999L),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                getValue());
        for (SimplifiedNode child : children) {
            freemindNode.getFreemindNodes().add(child.getFreemindInstance());
        }
        return freemindNode;
    }

    public String getValue() {
        return value;
    }

    public List<SimplifiedNode> getChildren() {
        return children;
    }
}
