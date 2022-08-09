package insa.lyon.justif.utils.algo.tsp;

import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.generated.model.Trip;
import insa.lyon.justif.model.TourTSP;

import java.util.*;

public class BranchAndBound implements TSP {

    private final Tour tourSolution = new Tour();

    private String[] bestSolution;
    private Double costBestSolution = Double.POSITIVE_INFINITY;

    private TourTSP tourTSP;

    @Override
    public Tour findSolution(int timeLimit, Tour tour) {
        this.tourTSP = new TourTSP(tour);

        bestSolution = new String[tourTSP.getIntersections().size()];

        List<String> unvisited = new ArrayList<>(tourTSP.getIntersections());
        List<String> visited = new ArrayList<>(tourTSP.getIntersections().size());

        unvisited.remove(tourTSP.getIntersections().getFirst());
        visited.add(tourTSP.getIntersections().getFirst());

        branchAndBound(tourTSP.getIntersections().getFirst(), unvisited, visited, 0.0);

        computeSolution();

        return tourSolution;
    }

    @Override
    public Tour getSolution() {
        return tourSolution;
    }

    @Override
    public Double getSolutionCost() {
        return costBestSolution;
    }

    /**
     * computeSolution : This method computes the trip of the solution tour from the list of intersections
     * contained in bestSolution (list of Intersection IDs).
     */
    private void computeSolution() {
        LinkedList<Trip> trips = new LinkedList<>();

        for (int i = 0; i < bestSolution.length - 1; i++) {
            String currentIntersection = bestSolution[i];
            String nextIntersection = bestSolution[i+1];
            Trip trip = new Trip();
            trip.setFrom(currentIntersection);
            trip.setTo(nextIntersection);
            trips.addLast(trip);
        }

        Trip depotEnd = new Trip();
        depotEnd.setFrom(bestSolution[bestSolution.length-1]);
        depotEnd.setTo(bestSolution[0]);
        trips.addLast(depotEnd);
        tourSolution.setTrips(trips);
    }

    /**
     * bound : This method computes a lower bound of the cost of the paths starting by the currentNode, visiting
     * all the unvisited nodes only once, and returning the Depot node.
     * The lower bound is at least the cost of the minimum incoming branch and in addition,
     * we add the cost of the minimum outgoing branch.
     * @param currentNode the current Node. (required)
     * @param unvisited the collection of the Intersections IDs of the unvisited nodes
     * @return the lower bound.
     */
    protected Double bound(String currentNode, Collection<String> unvisited) {
        Double lowerBound = 0.0;

        Double minimumIncomingBranch = Double.POSITIVE_INFINITY;
        for (String unvisitedIntersection : unvisited) {
            if(tourTSP.getTripLength(currentNode, unvisitedIntersection) < minimumIncomingBranch) {
                minimumIncomingBranch = tourTSP.getTripLength(currentNode, unvisitedIntersection);
            }
        }
        lowerBound += minimumIncomingBranch;

        for (String unvisitedIntersection : unvisited) {
            Double minimumOutGoingBranch = tourTSP.getTripLength(unvisitedIntersection, tourTSP.getIntersections().getFirst());
            for (String nextUnvisitedIntersection : unvisited) {
                if (!Objects.equals(unvisitedIntersection, nextUnvisitedIntersection) && tourTSP.getTripLength(unvisitedIntersection, nextUnvisitedIntersection) < minimumOutGoingBranch) {
                        minimumOutGoingBranch = tourTSP.getTripLength(unvisitedIntersection, nextUnvisitedIntersection);
                }
            }
            lowerBound += minimumOutGoingBranch;
        }

        return lowerBound;
    }

    protected Iterator<String> iterator(Collection<String> unvisited) {
        return new IteratorSeq(unvisited);
    }

    /**
     * branchAndBound : This method defines the pattern (template) of a resolution by separation and evaluation
     * (branch and bound algorithm) of the TSP.
     *
     * @param currentNode the last visited node. (required)
     * @param unvisited the list of nodes that have not been visited yet. (required)
     * @param visited the list of nodes that have been already visited (including the currentNode). (required)
     * @param currentVisitedCost  the cost of the path of the nodes that have been already visited
     *                            (taking into account the order in which they were visited). (required)
     */
    private void branchAndBound(String currentNode, Collection<String> unvisited, Collection<String> visited, Double currentVisitedCost) {
        if (unvisited.size() == 0) {
            double totalCost = currentVisitedCost + tourTSP.getTripLength(currentNode, tourTSP.getIntersections().getFirst());
            if (totalCost < costBestSolution) {
                visited.toArray(bestSolution);
                costBestSolution = totalCost;
            }
        } else if (currentVisitedCost + bound(currentNode, unvisited) < costBestSolution) {
            Iterator<String> it = iterator(unvisited);
            while (it.hasNext()){
                String nextIntersection = it.next();
                String pickup = tourTSP.getPickupPoint(nextIntersection);
                if(pickup == null || visited.contains(pickup)){
                    visited.add(nextIntersection);
                    unvisited.remove(nextIntersection);
                    branchAndBound(nextIntersection, unvisited, visited, currentVisitedCost + tourTSP.getTripLength(currentNode, nextIntersection));
                    visited.remove(nextIntersection);
                    unvisited.add(nextIntersection);
                }
            }
        }
    }

}
