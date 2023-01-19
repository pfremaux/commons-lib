package commons.lib.main.filestructure.graphviz.transformer;

import commons.lib.NodeV2;
import commons.lib.main.filestructure.graphviz.model.*;

import java.util.UUID;
import java.util.function.Function;

public class NodeToGraphViz<T> {

    private Function<T, GVNode> nodeConverter;
    private GVGraph.Builder builder;

    public static NodeToGraphViz<String> getConverterStringNodesToGraphViz(String graphName) {
        return new NodeToGraphViz<>(
                graphName,
                s -> new GVNode(UUID.randomUUID().toString(), s, GVShapeNode.BOX.toString().toLowerCase(), GVColor.BLUE, GVStyleText.BOLD, null, null));
    }

    public NodeToGraphViz(String graphName, Function<T, GVNode> nodeConverter) {
        this.nodeConverter = nodeConverter;
        this.builder = new GVGraph.Builder().setName(graphName);
    }

    public void convert(NodeV2<T> node) {

        if (node.isLeaf()) {
            final GVNode gvNode = nodeConverter.apply(node.getValue());
            builder.withNode(gvNode);
            if (node.getParent() != null) {
                final GVNode fromNode = nodeConverter.apply(node.getParent().getValue());
                final GVEdge edge = new GVEdge(fromNode, gvNode, GVColor.BLUE, GVStyleText.BOLD);
                builder.withEdge(edge);
            } else {
// TODO
            }
        }

    }
}
