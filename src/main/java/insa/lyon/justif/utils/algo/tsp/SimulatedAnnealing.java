package insa.lyon.justif.utils.algo.tsp;

import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.generated.model.Trip;
import insa.lyon.justif.model.TourTSP;

import java.util.LinkedList;

/**
 * SimulatedAnnealing : This class is a translation of the P. Portier's Simulated Annealing implementation in Java.
 */
public class SimulatedAnnealing implements TSP {

    private final double alpha = 0.98;
    private final double beta = 1.04;
    private final double beta0 = 0.0001;

    private double to;
    private int rejected = 0;
    private int maxTime;

    private Double minLength = Double.POSITIVE_INFINITY;
    private LinkedList<String> bestIntersections = new LinkedList<>();

    private TourTSP bestTour;

    @Override
    public Tour findSolution(int timeLimit, Tour tour) {
        bestTour = new TourTSP(tour);
        maxTime = timeLimit;

        heat(bestTour);
        simulatedAnnealing(bestTour);

        bestTour.setIntersectionIds(bestIntersections);

        return computeSolution(bestTour);
    }

    @Override
    public Tour getSolution() {
        return computeSolution(bestTour);
    }

    @Override
    public Double getSolutionCost() {
        return minLength;
    }

    /**
     * computeSolution : This method creates a tour from the list of intersections contained in a tour TSP.
     *
     * @param solution The TourTSP which contains the solution to the TSP (list of Intersections). (required)
     *
     * @return The tour created.
     */
    private Tour computeSolution(TourTSP solution) {
        LinkedList<Trip> trips = new LinkedList<>();
        for (int i = 0; i < solution.getIntersections().size() - 1; i++) {
            String currentIntersection = solution.getIntersections().get(i);
            String nextIntersection = solution.getIntersections().get(i+1);
            Trip trip = new Trip();
            trip.setFrom(currentIntersection);
            trip.setTo(nextIntersection);
            trips.addLast(trip);
        }
        Trip depotEnd = new Trip();
        depotEnd.setFrom(solution.getIntersections().getLast());
        depotEnd.setTo(solution.getIntersections().getFirst());
        trips.addLast(depotEnd);
        Tour computedSolution = new Tour();
        computedSolution.setTrips(trips);
        return computedSolution;
    }

    /**
     * simulatedAnnealing : This method implements the full "cooling" part of the Simulated Annealing process
     * using the simulatedAnnealingStep method several times.
     *
     * @param tour The initial Tour on which the permutations will be performed.
     */
    private void simulatedAnnealing(TourTSP tour) {
        boolean solutionFound = false;
        double currentLength = tour.getLengthByIntersections();
        double bestLengthAtTemp = minLength = currentLength;
        bestIntersections = new LinkedList<>(tour.getIntersections());
        double temperature = to;
        int elapsed = 0;
        int spentTimeAtTemperature = (int) Math.floor(beta0 * maxTime);
        int timer = spentTimeAtTemperature;
        while (elapsed < maxTime && !solutionFound && temperature >= 0.1) {
            bestLengthAtTemp = currentLength;
            while (timer != 0) {
                currentLength = simulatedAnnealingStep(tour, currentLength, temperature);
                if (currentLength == 0) {
                    bestLengthAtTemp = currentLength;
                    solutionFound = true;
                    break;
                } else if (currentLength < bestLengthAtTemp)  bestLengthAtTemp = currentLength;
                timer--;
            }
            elapsed += spentTimeAtTemperature;
            spentTimeAtTemperature = (int) Math.floor(spentTimeAtTemperature * beta);
            timer = spentTimeAtTemperature;
            temperature *= alpha;
        }
    }

    /**
     * heat : This method implements a step of the "cooling" part of the Simulated Annealing process.
     *
     * @param tour The initial Tour on which the permutations will be performed.
     * @param currentLentgh The length of the current permutation.
     * @param temperature The temperature at this step.
     */
    private Double simulatedAnnealingStep(TourTSP tour, Double currentLentgh, Double temperature) {
        double newLength;
        double deltaLength;
        tour.swap();
        newLength = tour.getLengthByIntersections();
        deltaLength = newLength - currentLentgh;
        if (deltaLength < 0) {
            currentLentgh = newLength;
            rejected = 0;
            if (currentLentgh < minLength) {
                minLength = currentLentgh;
                bestIntersections = new LinkedList<>(tour.getIntersections());
            }
        }
        else if (Math.random() < Math.exp( -deltaLength / temperature)) {
            currentLentgh = newLength;
            rejected = 0;
        }
        else {
            tour.revert();
            rejected = 1;
        }
        return currentLentgh;
    }

    /**
     * heat : This method implements the "heat" part of the Simulated Annealing process.
     * It allows in particular to find the initial temperature before starting the "cooling" part.
     *
     * @param tour The initial TourTSP (first permutation of Intersections). (required)
     */
    private void heat(TourTSP tour) {
        double currentLength = tour.getLengthByIntersections();
        double temperature = 1.0;
        double rejectProportion = 1.0;
        double rejectionThreshold = 0.05;
        int timer = 100;
        int maxIter = 1000;
        int nbReject;
        int k;
        int i;
        for (k = 0; k < maxIter && rejectProportion > rejectionThreshold; k++) {
            nbReject = 0;
            for (i = 0; i < timer; i++) {
                currentLength = simulatedAnnealingStep(tour, currentLength, temperature);
                nbReject += rejected;
            }
            rejectProportion = (double) nbReject / (double) timer;
            temperature *= 1.1;
        }
        if (k < maxIter) to = temperature;
    }

}
