package commons.lib.filestructure.freemind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class FreemindRoot {
    public static String DEFAULT_VERSION = "1.0.1";
    private final String version;
    private final List<FreemindNode> children;

    public FreemindRoot(String version) {
        this.version = version;
        this.children = new ArrayList<>();
    }

    public static FreemindRoot getDefaultInstance() {
        return new FreemindRoot(DEFAULT_VERSION);
    }

    public Element buildXmlEment(Document doc) {
        final Element map = doc.createElement("map");
        map.setAttribute("version", getVersion());
        for (FreemindNode node : getChildren()) {
            map.appendChild(node.buildXmlEment(doc, false));
        }
        return map;
    }

    public String getVersion() {
        return version;
    }

    public List<FreemindNode> getChildren() {
        return children;
    }
}
