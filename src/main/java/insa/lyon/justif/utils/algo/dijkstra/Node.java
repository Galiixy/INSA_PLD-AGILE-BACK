package insa.lyon.justif.utils.algo.dijkstra;

import java.util.HashMap;
import java.util.Map;

/**
 * Node : This class represents a Node of the Graph which is the support of the Dijkstra algorithm.
 */
public class Node {

    private final String idIntersection;

    private final Map<Double, Node> adjacentNodes = new HashMap<>();

    public Node(String idIntersection) {
        this.idIntersection = idIntersection;
    }

    public void addDestination(Node destination, Double distance) {
        adjacentNodes.put(distance, destination);
    }

    public String getIdIntersection() {
        return idIntersection;
    }

    public Map<Double, Node> getAdjacentNodes() { return adjacentNodes; }

}
