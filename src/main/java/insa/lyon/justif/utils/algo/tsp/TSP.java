package insa.lyon.justif.utils.algo.tsp;

import insa.lyon.justif.generated.model.Tour;

public interface TSP {

    /**
     * Find a solution to the TSP with the initial tour and a time limit given in input.
     * Note that the solution computed necessarily starts at the depot Node.
     * Attention : la solution calculee commence necessairement par le sommet 0
     *
     * @param timeLimit The time limit to compute the solution. (required)
     * @param tour The initial tour. (required)
     *
     * @return The tour which solves the TSP.
     */
    Tour findSolution(int timeLimit, Tour tour);

    /**
     * @return The tour which solves the TSP.
     */
    Tour getSolution();

    /**
     * @return The length of tour which solves the TSP.
     */
    Double getSolutionCost();

}
