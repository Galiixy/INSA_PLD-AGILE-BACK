package insa.lyon.justif.services;

import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.generated.model.Request;
import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.generated.model.Trip;
import insa.lyon.justif.utils.algo.tsp.BranchAndBound;
import insa.lyon.justif.utils.algo.tsp.Navigator;
import insa.lyon.justif.utils.algo.tsp.SimulatedAnnealing;
import insa.lyon.justif.utils.exceptions.InvalidStrategyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * TourService : Service class to compute the optimized tour.
 */
@Service
public class TourService {

    Navigator navigator = new Navigator();

    /**
     * computeOptimizedTour : Service to compute the optimized Tour with a specific
     * strategy given in input.
     *
     * @param tour Tour to optimize. This Tour contains the related Planning Request. (required)
     * @param tourStrategy The strategy to compute the tour. (required)
     *
     * @return The optimized tour computed.
     */
    public Tour computeOptimizedTour(Tour tour, String tourStrategy) throws InvalidStrategyException {
        int timelimit;
        switch (tourStrategy) {
            case "simulated-annealing":
                timelimit = 300000;
                navigator.setTourStrategy(new SimulatedAnnealing());
                break;
            case "branch-bound":
                timelimit = 10000;
                navigator.setTourStrategy(new BranchAndBound());
                break;
            default:
                throw new InvalidStrategyException("Unknown Strategy");
        }

        Tour initialTour = buildInitialTour(tour.getPlanning());

        Tour bestTour = navigator.findSolution(timelimit, initialTour);

        return fillTourInfo(bestTour, tour.getPlanning());
    }

    /**
     * fillTourInfo : Method to fill the trips information of a tour from the shortest paths
     * of the planning.
     *
     * @param tour Tour to fill. (required)
     * @param planningRequest Planning Request which contains the shortest paths. (required)
     *
     * @return The filled tour.
     */
    private Tour fillTourInfo(Tour tour, PlanningRequest planningRequest) {
        ArrayList<Trip> filledTrips = new ArrayList<>();
        for (Trip trip : tour.getTrips()) {
            filledTrips.add(planningRequest.getShortestTrips().get(trip.getFrom() + trip.getTo()));
        }
        tour.setTrips(filledTrips);
        tour.setPlanning(planningRequest);
        tour.setLength(getFullLength(tour));
        return tour;
    }

    /**
     * buildInitialTour : Method to create an initial tour from the Planning Request.
     * The initial tour is a simple concatenation of the pickup and delivery points of
     * the requests of the Planning Request.
     *
     * @param planningRequest Planning Request used to create an initial tour. (required)
     *
     * @return The initial tour built.
     */
    private Tour buildInitialTour(PlanningRequest planningRequest) {
        Tour tour = new Tour();
        tour.setPlanning(planningRequest);

        LinkedList<Trip> trips = new LinkedList<>();

        Trip depotEnd = new Trip();

        depotEnd.setTo(planningRequest.getDepot().getAddress());

        String previousTo = planningRequest.getDepot().getAddress();
        for(Request request : planningRequest.getRequests()) {
            Trip trip = new Trip();
            trip.setFrom(previousTo);
            trip.setTo(request.getPickupAddress());
            trips.addLast(trip);

            Trip trip2 = new Trip();
            trip2.setFrom(request.getPickupAddress());
            trip2.setTo(request.getDeliveryAddress());
            trips.addLast(trip2);

            previousTo = request.getDeliveryAddress();
        }

        depotEnd.setFrom(trips.getLast().getTo());

        trips.addLast(depotEnd);

        tour.setTrips(trips);

        tour.setLength(getFullLength(tour));

        return tour;
    }

    /**
     * getFullLength : Method to compute the full length of a tour thanks to its list of trips.
     *
     * @param tour Tour whose length will be computed. (required)
     *
     * @return The length of the tour.
     */
    private Double getFullLength(Tour tour) {
        Double length = 0.;
        for (Trip trip : tour.getTrips()) {
            length += tour.getPlanning().getShortestTrips().get(trip.getFrom() + trip.getTo()).getDistance();
        }
        return length;
    }

}
