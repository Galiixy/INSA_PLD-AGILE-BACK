package insa.lyon.justif.services;

import insa.lyon.justif.generated.model.*;
import insa.lyon.justif.utils.algo.dijkstra.Dijkstra;
import insa.lyon.justif.utils.algo.dijkstra.Graph;
import insa.lyon.justif.utils.algo.dijkstra.Node;
import io.swagger.annotations.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * GraphService : Service class to interact with the Graph of the shortest Paths.
 */
@Service
@Scope(name = "singleton", description = "")
public class GraphService {

    private final Graph graph = new Graph();

    private final Map<String, Trip> shortestTrips = new HashMap<>();

    private StreetMap streetMap;

    public void setStreetMap(StreetMap streetMap) {
        this.streetMap = streetMap;
    }


    /**
     * generateGraph : Service to generate a Node graph from a SreetMap where each Node represents an
     * Intersection of the StreetMap.
     */
    public Graph generateGraph() {
        Map<String, Node> nodes = new HashMap<>();
        for (Intersection intersection : streetMap.getIntersections().values()) {
            nodes.put(intersection.getId(), new Node(intersection.getId()));
        }
        for (Intersection intersection : streetMap.getIntersections().values()) {
                Node node = nodes.get(intersection.getId());
            for(Segment outgoingSegment : intersection.getOutgoingSegments()) {
                node.addDestination(nodes.get(outgoingSegment.getDestination()), outgoingSegment.getLength());
            }
        }
        for (Node node : nodes.values()) {
            graph.addNode(node, node.getIdIntersection());
        }
        return graph;
    }

    /**
     * getInterestNodes : Method to get the list of interest Nodes from a Planning Request.
     * An interest node can be a pickup node, a delivery node or the depot node.
     *
     * @param planningRequest The Planning Request to process. (required)
     *
     * @return The array list of Nodes of interest.
     */
    private ArrayList<Node> getInterestNodes(PlanningRequest planningRequest) {
        ArrayList<Node> interestNodes = new ArrayList<>();
        for (Request request : planningRequest.getRequests()) {
            Node n1 = graph.getNodes().get(request.getPickupAddress());
            Node n2 = graph.getNodes().get(request.getDeliveryAddress());
            interestNodes.add(n1);
            interestNodes.add(n2);
        }
        Node n = graph.getNodes().get(planningRequest.getDepot().getAddress());
        interestNodes.add(n);
        return interestNodes;
    }

    /**
     * buildShortestTrip : Method to build the Trip object which corresponds to the shortest path of Nodes
     * returned by the dijkstra algorithm.
     *
     * @param shortestPath The shortest path returned by the dijkstra algorithm. (required)
     *
     * @return The Trip which corresponds to the shortest path given in input.
     */
    private Trip buildShortestTrip(LinkedList<Node> shortestPath) {
        Trip shortestTrip = new Trip();
        shortestTrip.setFrom(shortestPath.get(0).getIdIntersection());
        shortestTrip.setTo(shortestPath.get(shortestPath.size()-1).getIdIntersection());
        Double totalDistance = 0.0;
        for (int i = 0 ; i < shortestPath.size()-1 ; i++) {
            Node n = shortestPath.get(i);
            Node nextNode = shortestPath.get(i+1);
            List<Segment> segments = streetMap.getIntersections().get(n.getIdIntersection()).getOutgoingSegments();
            for (Segment segment : segments) {
                if (Objects.equals(segment.getDestination(), nextNode.getIdIntersection())) {
                    shortestTrip.addSegmentsItem(segment);
                    totalDistance += segment.getLength();
                    break;
                }
            }
        }
        shortestTrip.setDistance(totalDistance);
        return shortestTrip;
    }

    /**
     * computeShortestPaths : Service to compute all the shortest paths based on a Planning Request.
     * Indeed, this function computes all the shortest paths between the different Nodes of interest using
     * the dijkstra algorithm.
     *
     * @param planningRequest The Planning Request to process. (required)
     */
    public Map<String, Trip> computeShortestPaths(PlanningRequest planningRequest) {
        ArrayList<Node> interestNodes = getInterestNodes(planningRequest);
        for (Node n1 : interestNodes) {
            for (Node n2 : interestNodes) {
                if (!Objects.equals(n1.getIdIntersection(), n2.getIdIntersection())) {
                    LinkedList<Node> shortestPath = Dijkstra.dijkstra(n1, n2);
                    Trip shortestTrip = buildShortestTrip(shortestPath);
                    shortestTrips.put(shortestTrip.getFrom() + shortestTrip.getTo(), shortestTrip);
                }
            }
        }
        planningRequest.setShortestTrips(shortestTrips);
        return shortestTrips;
    }

}

