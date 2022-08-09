package insa.lyon.justif.service;

import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.generated.model.Trip;
import insa.lyon.justif.services.GraphService;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.utils.algo.dijkstra.Graph;
import insa.lyon.justif.utils.algo.dijkstra.Node;
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

import java.util.Map;

import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GraphServiceTest {

    private final MapService mapService = new MapService();

    private final PlanningService planningService = new PlanningService();

    @MockBean
    private StreetMap streetMapMock;

    @InjectMocks
    private final GraphService graphService = new GraphService();

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        try {
            mapService.parseMapXml("src/main/resources/xml/smallMap.xml");
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsSmall1.xml");
        } catch (ParsingXMLException ignored) {
        }
    }

    @Test
    public void generateGraphTest() {
        when(streetMapMock.getIntersections()).thenReturn(mapService.getStreetMap().getIntersections());
        Graph graph = graphService.generateGraph();
        assert graph != null;
        for (Node node : graph.getNodes().values()) {
            assert node.getIdIntersection() != null;
            assert node.getAdjacentNodes() != null;
        }
    }

    @Test
    public void computeShortestPathsTest() {
        when(streetMapMock.getIntersections()).thenReturn(mapService.getStreetMap().getIntersections());
        graphService.generateGraph();
        Map<String, Trip> shortestTrips = graphService.computeShortestPaths(planningService.getPlanningRequest());
        assert shortestTrips != null;
        for (Trip trip : shortestTrips.values()) {
            assert trip.getFrom() != null;
            assert trip.getTo() != null;
            assert trip.getDistance() != null;
            assert trip.getSegments() != null;
        }
    }


}
