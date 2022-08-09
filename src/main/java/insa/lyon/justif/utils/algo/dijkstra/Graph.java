package insa.lyon.justif.utils.algo.dijkstra;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph : This class represents the support of the Dijkstra algorithm.
 */
public class Graph {

    private Map<String, Node> nodes = new HashMap<>();

    public void addNode(Node node, String id) {
        nodes.put(id, node);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

}
