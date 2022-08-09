package insa.lyon.justif.services;

import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.generated.model.Request;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidPlanningScopeException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import insa.lyon.justif.utils.xml.XmlObjectMapper;
import insa.lyon.justif.utils.xml.XmlValidator;
import io.swagger.annotations.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * PlanningService : Service class to interact with the Planning Request object.
 */
@Service
@Scope(name = "singleton", description = "")
public class PlanningService {

    private PlanningRequest planningRequest;



    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }


    /**
     * parsePlanningRequestXml : Service to parse an XML file which contains a Planning Request.
     *
     * @param xmlPath Path of the XML file to parse. (required)
     */
    public void parsePlanningRequestXml(String xmlPath) throws ParsingXMLException {
        XmlObjectMapper xmlMapper = new XmlObjectMapper(xmlPath);
        planningRequest = xmlMapper.planningRequestXmlMapper();
    }

    /**
     * isValidPlanningXml : Service to validate an XML file which contains a Planning Request thanks to the corresponding XSD file.
     * It throws an InvalidXMLException if the structure of the XML file doesn't fit with the XSD schema.
     *
     * @param planningXmlPath Path of the XML file to validate. (required)
     */
    public void isValidPlanningXml(String planningXmlPath) throws InvalidXMLException {
        InputStream planningXSD = MapService.class.getResourceAsStream("/xsd/planning.xsd");
        XmlValidator xmlValidator = new XmlValidator(planningXmlPath, planningXSD);
        xmlValidator.validateXmlWithXSD();
    }

    /**
     * isValidPlanningXmlFileName : Service to check if the name of a file has an XML extension.
     * If the file is note an XML file, then the service throws an InvalidFileExtensionException.
     *
     * @param planningXmlName Name of the file to validate. (required)
     */
    public void isValidPlanningXmlFileName(String planningXmlName) throws InvalidFileExtensionException {
        XmlValidator xmlValidator = new XmlValidator(planningXmlName);
        xmlValidator.validateXmlPath();
    }

    /**
     * isValidPlanningScope : Service to check if the scope of a Planning request is larger than
     * the scope of the StreetMap uploaded.
     * If there is a problem of scope, then the service throws an InvalidPlanningScopeException.
     */
    public void isValidPlanningScope(StreetMap streetMap) throws InvalidPlanningScopeException {
        InvalidPlanningScopeException scopeException = new InvalidPlanningScopeException("Invalid Planning scope for the Street Map uploaded.");
        if (streetMap.getIntersections().get(planningRequest.getDepot().getAddress()) == null) {
            throw scopeException;
        }
        for (Request request : planningRequest.getRequests()) {
           if (streetMap.getIntersections().get(request.getPickupAddress()) == null || streetMap.getIntersections().get(request.getDeliveryAddress()) == null) {
                throw scopeException;
           }
        }
    }

}
