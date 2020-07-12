package commons.lib.filestructure.freemind;

import commons.lib.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class FreemindNode {
    private final String id;
    private final long created;
    private final long modified;
    private final String text;
    private final List<FreemindNode> freemindNodes = new ArrayList<>();

    public FreemindNode(String id, long created, long modified, String text) {
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public String getText() {
        return text;
    }

    public List<FreemindNode> getFreemindNodes() {
        return freemindNodes;
    }

    public List<String> toLines(int level) {
        final List<String> strings = new ArrayList<>();
        final StringBuilder tab = new StringBuilder();
        tab.append("\t".repeat(Math.max(0, level)));
        strings.add(tab.toString() + getId() + " " + getText());
        for (FreemindNode freemindNode : getFreemindNodes()) {
            strings.addAll(freemindNode.toLines(level + 1));
        }
        return strings;
    }

    public Element buildXmlEment(Document doc, boolean position) {
        final Element node = doc.createElement("node");
        node.setAttribute("CREATED", Long.toString(getCreated()));
        node.setAttribute("MODIFIED", Long.toString(getModified()));
        node.setAttribute("ID", getId());
        node.setAttribute("TEXT", StringUtils.escapeToHtml(getText()));
        node.setAttribute("POSITION", position ? "right": "left");
        boolean childrenPosition = false;
        for (FreemindNode freemindNode : getFreemindNodes()) {
            Element element = freemindNode.buildXmlEment(doc, childrenPosition);
            childrenPosition = !childrenPosition;
            node.appendChild(element);
        }
        return node;
    }
}
