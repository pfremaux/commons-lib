package commons.lib.main.filestructure.freemind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Freemind {

    // run with -Dconsole.encoding=UTF-8 -Dfile.encoding=UTF-8
    public static void main(String argv[]) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        final String pathname = "E:\\Projet J\\00- Requirements\\Brainstorm.mm";
        final FreemindRoot rootNodes = loadFreemindFile(pathname);
        for (FreemindNode rootNode : rootNodes.getChildren()) {
            final List<String> strings = rootNode.toLines(0);
            for (String string : strings) {
                System.out.println(string);
            }
        }
        saveFreemindFile("E:\\Projet J\\00- Requirements\\BrainstormCopy.mm", rootNodes);
    }

    public static FreemindRoot loadFreemindFile(String pathname) throws ParserConfigurationException, SAXException, IOException {
        final File fXmlFile = new File(pathname);
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        // TODO maybe do this part in a static method in FreemindRoot
        Element map = doc.getDocumentElement();
        String version = map.getAttribute("version");
        FreemindRoot freemindRoot = new FreemindRoot(version);
        final NodeList nodeList = doc.getDocumentElement().getChildNodes();
        final List<FreemindNode> freemindNodes = getFreemindNodes(nodeList);
        freemindRoot.getChildren().addAll(freemindNodes);
        return freemindRoot;
    }

    public static void saveFreemindFile(String pathname, FreemindRoot root) throws ParserConfigurationException, TransformerException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element rootElement = root.buildXmlEment(doc);
        doc.appendChild(rootElement);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource xmlSource = new DOMSource(doc);

        // Don't generate the <?xml> tag otherwise Freemind complains about version error.
        // You won't be stuck with that but it will ask you to upgrade your file.
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        final StreamResult resultFile = new StreamResult(new File(pathname));
        transformer.transform(xmlSource, resultFile);
    }

    // TODO maybe put it in a static method in FreemindNode
    private static List<FreemindNode> getFreemindNodes(NodeList nodeList) {
        final List<FreemindNode> currentLevelNodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node item = nodeList.item(i);
            final FreemindNode freemindNode = convertFreemindNodeIsolated(item);
            if (freemindNode != null) {
                currentLevelNodes.add(freemindNode);
                List<FreemindNode> freemindNodes = getFreemindNodes(item.getChildNodes());
                freemindNode.getFreemindNodes().addAll(freemindNodes);
            }
        }
        return currentLevelNodes;
    }

    // TODO maybe put it in a static method in FreemindNode
    private static FreemindNode convertFreemindNodeIsolated(Node item) {
        // New lines are considered as nodes with no attributes, we return null
        if (item.getAttributes() == null) {
            return null;
        }
        final Node created = item.getAttributes().getNamedItem("CREATED");
        if (created == null) {
            return null;
        }
        final long nodeValueCreated = Long.parseLong(created.getNodeValue());
        final Node id = item.getAttributes().getNamedItem("ID");
        final String nodeValueId = id.getNodeValue();
        final Node modified = item.getAttributes().getNamedItem("MODIFIED");
        final long nodeValueModified = Long.parseLong(modified.getNodeValue());
        final Node text = item.getAttributes().getNamedItem("TEXT");
        final String nodeValueText = text.getNodeValue();
        return new FreemindNode(nodeValueId, nodeValueCreated, nodeValueModified, nodeValueText);
    }

}
