package insa.lyon.justif.services;

import insa.lyon.justif.generated.model.Intersection;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import insa.lyon.justif.utils.xml.XmlObjectMapper;
import insa.lyon.justif.utils.xml.XmlValidator;
import io.swagger.annotations.Scope;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * MapService : Service class to interact with the Street Map object.
 */
@Service
@Scope(name = "singleton", description = "")
public class MapService {

    private StreetMap streetMap;

    public StreetMap getStreetMap() {
        return streetMap;
    }

    public void setStreetMap(StreetMap streetMap) {
        this.streetMap = streetMap;
    }


    /**
     * parseMapXml : Service to parse an XML file which contains a Planning Request.
     *
     * @param xmlPath Path of the XML file to parse. (required)
     */
    public void parseMapXml(String xmlPath) throws ParsingXMLException {
        XmlObjectMapper xmlMapper = new XmlObjectMapper(xmlPath);
        streetMap = xmlMapper.streetMapXmlMapper();
    }

    /**
     * getIntersectionById : Service to get all the information about an Intersection of the StreetMap.
     * If the Intersection ID given in input doesn't belong to the StreetMap, then the service returns null.
     *
     * @param id The id of the Intersection. (required)
     * @return The corresponding Intersection or null.
     */
    public Intersection getIntersectionById(String id) {
        return streetMap.getIntersections().get(id);
    }

    /**
     * setMapBounds : Service to compute and set the bounds of the StreetMap.
     * The bounds of the StreetMap are important to display it correctly on the web interface.
     */
    public void setMapBounds() {
        double latitudeMin= Double.MAX_VALUE;
        double latitudeMax= Double.MIN_VALUE;
        double longitudeMin= Double.MAX_VALUE;
        double longitudeMax= Double.MIN_VALUE;
        for (Intersection intersection:streetMap.getIntersections().values()) {
            latitudeMin=(intersection.getLatitude() < latitudeMin)?intersection.getLatitude():latitudeMin;
            latitudeMax=(intersection.getLatitude() > latitudeMax)? intersection.getLatitude() : latitudeMax;
            longitudeMin=(intersection.getLongitude() < longitudeMin)?intersection.getLongitude():longitudeMin;
            longitudeMax=(intersection.getLongitude() > longitudeMax)? intersection.getLongitude() : longitudeMax;
        }
        streetMap.setLatitudeMin(latitudeMin);
        streetMap.setLatitudeMax(latitudeMax);
        streetMap.setLongitudeMin(longitudeMin);
        streetMap.setLongitudeMax(longitudeMax);
    }

    /**
     * isValidMapXml : Service to validate an XML file using the corresponding XSD file.
     * It throws an InvalidXMLException if the structure of the XML file doesn't fit with the XSD schema.
     *
     * @param mapXmlPath Path of the XML file of the Street Map. (required)
     */
    public void isValidMapXml(String mapXmlPath) throws InvalidXMLException {
        InputStream mapXSD = MapService.class.getResourceAsStream("/xsd/map.xsd");
        XmlValidator xmlValidator = new XmlValidator(mapXmlPath, mapXSD);
        xmlValidator.validateXmlWithXSD();
    }

    /**
     * isValidMapXmlFileName : Service to check if the name of a file has an XML extension.
     * If the file is note an XML file, then the service throws an InvalidFileExtensionException.
     *
     * @param mapXmlName Name of the file of the Street Map. (required)
     */
    public void isValidMapXmlFileName(String mapXmlName) throws InvalidFileExtensionException {
        XmlValidator xmlValidator = new XmlValidator(mapXmlName);
        xmlValidator.validateXmlPath();
    }

}
