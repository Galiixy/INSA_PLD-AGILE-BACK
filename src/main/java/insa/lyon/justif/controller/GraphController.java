package insa.lyon.justif.controller;


import com.fasterxml.jackson.annotation.JsonInclude;
import insa.lyon.justif.generated.apis.GraphApi;
import insa.lyon.justif.services.GraphService;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GraphController : Controller for requests on /graph endpoint.
 * This endpoint allows the front-end to interact with the Graph object.
 */
@Api(value = "graph")
@RestController
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@CrossOrigin
public class GraphController implements GraphApi {

    @Autowired
    GraphService graphService;

    @Autowired
    MapService mapService;

    @Autowired
    PlanningService planningService;

    /**
     * POST /graph : Generate graph from StreetMap
     * Generate graph from StreetMap
     *
     * @return Successful operation. (status code 200)
     */
    @ApiOperation(value = "Generate graph from StreetMap", nickname = "generateGraph", notes = "Generate graph from StreetMap", tags={ "graph", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.") })
    @PostMapping(
            value = "/graph"
    )
    @Override
    public ResponseEntity<Void> generateGraph() {
        graphService.setStreetMap(mapService.getStreetMap());
        graphService.generateGraph();
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * POST /graph/shortest-paths : Generate all shortest paths from graph
     * Generate all shortest paths from graph
     *
     * @return Successful operation. (status code 200)
     */
    @ApiOperation(value = "Generate all shortest paths from graph", nickname = "shortestPathsGraph", notes = "Generate all shortest paths from graph", tags={ "graph", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.") })
    @PostMapping(
            value = "/graph/shortest-paths"
    )
    @Override
    public ResponseEntity<Void> shortestPathsGraph() {
        graphService.computeShortestPaths(planningService.getPlanningRequest());
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
