package insa.lyon.justif.service;

import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.generated.model.Trip;
import insa.lyon.justif.services.GraphService;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.services.TourService;
import insa.lyon.justif.utils.exceptions.InvalidStrategyException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TourServiceTest {

    private final TourService tourService = new TourService();
    private final MapService mapService = new MapService();
    private final PlanningService planningService = new PlanningService();

    @InjectMocks
    private GraphService graphService = new GraphService();

    @MockBean
    private StreetMap streetMapMock;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private void validateBuiltTour(Tour tour) {
        for (Trip trip : tour.getTrips()) {
            assert trip.getTo() != null;
            assert trip.getFrom() != null;
            assert trip.getSegments() != null;
            assert trip.getDistance() != null;
        }
    }

    private void runComputeOptimizedTest(String mapXml, String planningXml, String strategy) {
        boolean exceptionThrown = false;
        try {
            mapService.parseMapXml("src/main/resources/xml/" + mapXml);
            planningService.parsePlanningRequestXml("src/main/resources/xml/" + planningXml);
        } catch (ParsingXMLException ignored) {
        }
        when(streetMapMock.getIntersections()).thenReturn(mapService.getStreetMap().getIntersections());
        graphService.generateGraph();
        graphService.computeShortestPaths(planningService.getPlanningRequest());
        Tour tour = new Tour().planning(planningService.getPlanningRequest());
        Tour optimized = null;
        try {
            optimized = tourService.computeOptimizedTour(tour, strategy);
        } catch (InvalidStrategyException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        assert optimized != null;
        validateBuiltTour(optimized);
    }

    @Test
    public void computeOptimizedTourBranchAndBoundTest() {
        ArrayList<String> maps = new ArrayList<>(Arrays.asList("smallMap", "mediumMap", "largeMap"));
        ArrayList<String> plannings = new ArrayList<>(Arrays.asList("requestsSmall2", "requestsMedium5", "requestsLarge7"));
        Iterator<String> it1 = maps.iterator();
        Iterator<String> it2 = plannings.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            runComputeOptimizedTest(it1.next() + ".xml", it2.next() + ".xml", "branch-bound");
        }
    }

    @Test
    public void computeOptimizedTourSimulatedAnnealingSmallTest() {
        ArrayList<String> maps = new ArrayList<>(Arrays.asList("smallMap", "mediumMap", "largeMap"));
        ArrayList<String> plannings = new ArrayList<>(Arrays.asList("requestsSmall2", "requestsMedium5", "requestsLarge7"));
        Iterator<String> it1 = maps.iterator();
        Iterator<String> it2 = plannings.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            runComputeOptimizedTest(it1.next() + ".xml", it2.next() + ".xml", "simulated-annealing");
        }
    }


}
