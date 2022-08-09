package insa.lyon.justif.utils.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import insa.lyon.justif.generated.model.*;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class XmlObjectMapper {

    private final String xmlPath;

    private final ObjectMapper xmlMapper;

    public XmlObjectMapper(String xmlPath) {
        this.xmlPath = xmlPath;
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        this.xmlMapper = new XmlMapper(xmlModule);
    }

    /**
     * streetMapXmlMapper : This method allows to parse an XML file which corresponds to a Street Map.
     *
     * @return The StreetMap object built by parsing of the XML file.
     */
    public StreetMap streetMapXmlMapper() throws ParsingXMLException {
        StreetMap streetMap = new StreetMap();
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlPath))) {
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("intersection")) {
                    Intersection intersection = xmlMapper.readValue(line, Intersection.class);
                    intersection.setOutgoingSegments(new ArrayList<>());
                    streetMap.putIntersectionsItem(intersection.getId(), intersection);
                }
                if (line.contains("segment")) {
                    Segment segment = xmlMapper.readValue(line, Segment.class);
                    streetMap.getIntersections().get(segment.getOrigin()).getOutgoingSegments().add(segment);
                }
                line = reader.readLine();
            }

        } catch (Exception ex) {
            throw new ParsingXMLException(ex.getMessage());
        }
        return streetMap;
    }

    /**
     * planningRequestXmlMapper : This method allows to parse an XML file which corresponds to a Planning Request.
     *
     * @return The PlanningRequest object built by parsing of the XML file.
     */
    public PlanningRequest planningRequestXmlMapper() throws ParsingXMLException {
        PlanningRequest planningRequest = new PlanningRequest();
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlPath))) {
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("depot")) {
                    Depot depot = xmlMapper.readValue(line, Depot.class);
                    planningRequest.setDepot(depot);
                }
                if (line.contains("request")) {
                    Request request = xmlMapper.readValue(line, Request.class);
                    planningRequest.addRequestsItem(request);
                }
                line = reader.readLine();
            }
        } catch (Exception ex) {
            throw new ParsingXMLException(ex.getMessage());
        }
        return planningRequest;
    }

}
