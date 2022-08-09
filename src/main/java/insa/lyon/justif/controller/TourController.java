package insa.lyon.justif.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import insa.lyon.justif.generated.apis.TourApi;
import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.services.TourService;
import insa.lyon.justif.utils.exceptions.InvalidStrategyException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * TourController : Controller for requests on /tour endpoint.
 * This endpoint allows the front-end to interact with the Tour computation.
 */
@Api(value = "tour")
@RestController
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@CrossOrigin
public class TourController implements TourApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TourController.class);

    private static final String LOGGER_PREFIX = "[Tour]";

    @Autowired
    TourService tourService;

    @Autowired
    PlanningService planningService;

    /**
     * GET /tour/{tourStrategy} : Get the optimized tour for the planning request uploaded.
     *
     * @param tourStrategy Strategy used to compute the optimized Tour. (required)
     * @return Successful operation. (status code 200)
     *         or Unknown strategy given in input. (status code 400)
     */
    @ApiOperation(value = "Get the optimized tour for the planning request uploaded.", nickname = "getTour", notes = "", response = Tour.class, tags={ "tour", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.", response = Tour.class),
            @ApiResponse(code = 400, message = "Unknown strategy given in input.") })
    @GetMapping(
            value = "/tour/{tourStrategy}",
            produces = { "application/json" }
    )
    @Override
    public ResponseEntity<Tour> getTour(@ApiParam(value = "Strategy used to compute the optimized Tour.", required = true) @PathVariable("tourStrategy") String tourStrategy) {
        Tour tour = new Tour().planning(planningService.getPlanningRequest());
        Tour optimized;
        try {
            optimized = tourService.computeOptimizedTour(tour, tourStrategy);
        } catch (InvalidStrategyException e) {
            LOGGER.error(LOGGER_PREFIX + "[Strategy] - Unknown Strategy : " + tourStrategy);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(optimized);
    }
}
