package insa.lyon.justif.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import insa.lyon.justif.generated.apis.PlanningApi;
import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.utils.Utils;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidPlanningScopeException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * PlanningController : Controller for requests on /planning endpoint.
 * This endpoint allows the front-end to interact with the Planning Request object.
 */
@Api(value = "planning")
@RestController
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@CrossOrigin
public class PlanningController implements PlanningApi {

    private static final String XML_FOLDER = System.getProperty("user.dir") + "/files/";

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanningController.class);

    private static final String LOGGER_PREFIX = "[Planning]";

    @Autowired
    PlanningService planningService;

    @Autowired
    private MapService mapService;

    /**
     * POST /planning : Upload a planning file.
     * The file must to be an XML file and follows the Planning Request format.
     *
     * @param file XML file which represents the planning requests. (required)
     * @return Successful operation. (status code 200)
     *         or Invalid file for upload. (status code 400)
     *         or Invalid XML file. (status code 422)
     *         or Error while reading or parsing the uploaded file. (status code 500)
     */
    @ApiOperation(value = "Upload a planning file.", nickname = "uploadPlanning", notes = "The file must to be an XML file and follows the Planning Request format.", response = PlanningRequest.class, tags={ "planning", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.", response = PlanningRequest.class),
            @ApiResponse(code = 400, message = "Invalid file for upload."),
            @ApiResponse(code = 422, message = "Invalid XML file."),
            @ApiResponse(code = 500, message = "Error while reading or parsing the uploaded file.") })
    @PostMapping(
            value = "/planning",
            produces = { "application/json" },
            consumes = { "multipart/form-data" }
    )
    @Override
    public ResponseEntity<PlanningRequest> uploadPlanning(@ApiParam(value = "XML file which represents the planning requests.") @Valid @RequestPart(value = "file", required = true) MultipartFile file) {
        String fileFullPath = XML_FOLDER + file.getOriginalFilename();
        if (file.isEmpty()) {
            LOGGER.error(LOGGER_PREFIX + "[File Upload] - Can't upload an empty file.");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            planningService.isValidPlanningXmlFileName(fileFullPath);
            Utils.saveUploadedFile(file, XML_FOLDER);
            planningService.isValidPlanningXml(fileFullPath);
            planningService.parsePlanningRequestXml(fileFullPath);
            planningService.isValidPlanningScope(mapService.getStreetMap());
        } catch (InvalidFileExtensionException ex) {
            LOGGER.error("[File Upload] - Can't upload file which is not XML file.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException ex) {
            LOGGER.error(LOGGER_PREFIX + "[File Upload] - An I/O error as occurred while uploading the file.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidXMLException ex) {
            LOGGER.error(LOGGER_PREFIX + "[File Validation] - The XML file uploaded doesn't respect the expected format.");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ParsingXMLException ex) {
            LOGGER.error(LOGGER_PREFIX + "[File Parsing] - An error as occurred while parsing the XML file.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidPlanningScopeException ex) {
            LOGGER.error(LOGGER_PREFIX + "[File Scope] - Invalid planning scope for the Street Map uploaded.");
            return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        }
        return ResponseEntity.ok(planningService.getPlanningRequest());
    }

}
