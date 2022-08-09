package insa.lyon.justif.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import insa.lyon.justif.generated.apis.MapApi;
import insa.lyon.justif.generated.model.Intersection;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.utils.Utils;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MapController : Controller for requests on /map endpoint.
 * This endpoint allows the front-end to interact with the Street Map object.
 */
@Api(value = "map")
@RestController
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
@CrossOrigin
public class MapController implements MapApi {

    private static final String XML_FOLDER = System.getProperty("user.dir") + "/files/";

    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    private static final String LOGGER_PREFIX = "[Map]";

    @Autowired
    MapService mapService;

    /**
     * POST /map/intersection : Get information about intersections of the Street Map.
     *
     * @param intersectionsIds IDs of the intersections. (required)
     * @return Successful operation. (status code 200)
     *         or Intersection not found. (status code 404)
     */
    @ApiOperation(value = "Get information about intersections of the Street Map.", nickname = "getIntersectionsById", notes = "", response = Intersection.class, responseContainer = "List", tags={ "map", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.", response = Intersection.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Intersection not found.") })
    @PostMapping(
            value = "/map/intersection",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    @Override
    public ResponseEntity<List<Intersection>> getIntersectionsById(@ApiParam(value = "IDs of the intersections." ,required=true )  @Valid @RequestBody List<String> intersectionsIds) {
        ArrayList<Intersection> intersections = new ArrayList<>();
        for(String intersectionId : intersectionsIds) {
            Intersection intersection = mapService.getIntersectionById(intersectionId);
            if (intersection != null) intersections.add(intersection);
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(intersections);
    }


    /**
     * POST /map : Upload a map file.
     * The file must to be an XML file and follows the Street Map format.
     *
     * @param file XML file which represents the map. (required)
     * @return Successful operation. (status code 200)
     * or Invalid file for upload. (status code 400)
     * or Invalid XML file. (status code 422)
     * or Error while reading or parsing the uploaded file. (status code 500)
     */
    @ApiOperation(value = "Upload a map file.", nickname = "uploadMap", notes = "The file must to be an XML file and follows the Street Map format.", response = StreetMap.class, tags = {"map",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation.", response = StreetMap.class),
            @ApiResponse(code = 400, message = "Invalid file for upload."),
            @ApiResponse(code = 422, message = "Invalid XML file."),
            @ApiResponse(code = 500, message = "Error while reading or parsing the uploaded file.")})
    @PostMapping(
            value = "/map",
            produces = {"application/json"},
            consumes = {"multipart/form-data"}
    )
    @Override
    public ResponseEntity<StreetMap> uploadMap(@Valid @RequestPart(value = "file", required = true) MultipartFile file) {
        String fileFullPath = XML_FOLDER + file.getOriginalFilename();
        if (file.isEmpty()) {
            LOGGER.error(LOGGER_PREFIX + "[File Upload] - Can't upload an empty file.");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        try {
            mapService.isValidMapXmlFileName(fileFullPath);
            Utils.saveUploadedFile(file, XML_FOLDER);
            mapService.isValidMapXml(fileFullPath);
            mapService.parseMapXml(fileFullPath);
            mapService.setMapBounds();
        } catch (InvalidFileExtensionException ex) {
            LOGGER.error(LOGGER_PREFIX + "[File Upload] - Can't upload file which is not XML file.");
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
        }
        return ResponseEntity.ok(mapService.getStreetMap());
    }
}
