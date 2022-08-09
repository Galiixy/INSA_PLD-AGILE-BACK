package insa.lyon.justif.utils.algo.dijkstra;

import org.springframework.data.util.Pair;

import java.util.*;

/**
 * Dijkstra : This class is a translation of the P. Portier's Dijkstra implementation in Java.
 */
public class Dijkstra {

    /**
     * dijkstra : This method computes the shortest path between a source and a target node using the
     * dijkstra algorithm.
     *
     * @param source The source Node of the path to compute. (required)
     * @param target The target Node of the path to compute. (required)
     *
     * @return The shortest path of Nodes from the source Node to the target Node.
     */
    public static LinkedList<Node> dijkstra(Node source, Node target) {
        LinkedList<Node> path = new LinkedList<>();
        Map<Node, Node> parent = new HashMap<>();
        parent.put(source, source);
        Map<Node, Double> distances = new HashMap<>();
        distances.put(source, 0.0);
        LinkedList<Node> exploredNodes = new LinkedList<>();
        PriorityQueue<Pair<Double, Node>> nodesToExplore = new PriorityQueue<>(new Comparator<Pair<Double, Node>>() {
            @Override
            public int compare(Pair<Double, Node> o1, Pair<Double, Node> o2) {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });
        nodesToExplore.add(Pair.of(0.0, source));

        while (!nodesToExplore.isEmpty()) {
            Node unexploredNode = nodesToExplore.peek().getSecond();
            if (target == unexploredNode) {
                while (unexploredNode != source) {
                    path.addFirst(unexploredNode);
                    unexploredNode = parent.get(unexploredNode);
                }
                path.addFirst(source);
                return path;
            }
            nodesToExplore.poll();
            exploredNodes.add(unexploredNode);

            for (Map.Entry<Double, Node> n : unexploredNode.getAdjacentNodes().entrySet()) {
                if (exploredNodes.contains(n.getValue())) continue;
                if (getDistance(n.getValue(), distances) > distances.get(unexploredNode) + n.getKey()) {
                    distances.put(n.getValue(), distances.get(unexploredNode) + n.getKey());
                    parent.put(n.getValue(), unexploredNode);
                    nodesToExplore.add(Pair.of(distances.get(n.getValue()),n.getValue()));
                }
            }
        }
        return path;
    }

    /**
     * getDistance : This method returns the distance to a Node if the path to this Node exists
     * or the max value of a Double if not.
     *
     * @param v The target Node. (required)
     * @param distances The map of all the possible paths with their cost (distance). (required)
     *
     * @return The distance to a Node if the path to this Node exists or the max value of a Double if not.
     */
    private static Double getDistance(Node v, Map<Node, Double> distances){
        return distances.getOrDefault(v, Double.MAX_VALUE);
    }

}
