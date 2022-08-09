package insa.lyon.justif.utils.algo.tsp;

import insa.lyon.justif.generated.model.Tour;

/**
 * Navigator : This class is a part of the Strategy Design Pattern. It represents the "Context" which
 * allows to store the current Strategy.
 */
public class Navigator implements TSP {

    private TSP tourStrategy;

    public void setTourStrategy(TSP tourStrategy) {
        this.tourStrategy = tourStrategy;
    }

    @Override
    public Tour findSolution(int timeLimit, Tour tour) {
        return tourStrategy.findSolution(timeLimit, tour);
    }

    @Override
    public Tour getSolution() {
        return tourStrategy.getSolution();
    }

    @Override
    public Double getSolutionCost() {
        return tourStrategy.getSolutionCost();
    }

}
