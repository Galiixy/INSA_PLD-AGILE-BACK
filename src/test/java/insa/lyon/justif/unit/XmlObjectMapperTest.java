package insa.lyon.justif.unit;

import insa.lyon.justif.generated.model.Intersection;
import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.generated.model.Request;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import insa.lyon.justif.utils.xml.XmlObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class XmlObjectMapperTest {

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void streetMapValidXmlMapper() {
        boolean exceptionThrown = false;
        XmlObjectMapper xmlObjectMapper = new XmlObjectMapper("src/main/resources/xml/largeMap.xml");
        StreetMap streetMap = null;
        try {
            streetMap = xmlObjectMapper.streetMapXmlMapper();
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        assert streetMap != null;
        for (Intersection intersection : streetMap.getIntersections().values()) {
            assert intersection.getId() != null;
            assert intersection.getLatitude() != null;
            assert intersection.getLongitude() != null;
            assert intersection.getOutgoingSegments() != null;
        }
    }

    @Test
    public void streetMapInvalidXmlMapper() {
        boolean exceptionThrown = false;
        XmlObjectMapper xmlObjectMapper = new XmlObjectMapper("src/main/resources/xml/invalidMap.xml");
        try {
            xmlObjectMapper.streetMapXmlMapper();
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    @Test
    public void planningRequestValidXmlMapper() {
        boolean exceptionThrown = false;
        XmlObjectMapper xmlObjectMapper = new XmlObjectMapper("src/main/resources/xml/requestsLarge9.xml");
        PlanningRequest planningRequest = null;
        try {
            planningRequest = xmlObjectMapper.planningRequestXmlMapper();
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        assert planningRequest != null;
        for (Request request : planningRequest.getRequests()) {
            assert request.getPickupAddress() != null;
            assert request.getPickupDuration() != null;
            assert request.getDeliveryAddress() != null;
            assert request.getDeliveryDuration() != null;
        }
        assert planningRequest.getDepot().getDepartureTime() != null;
        assert planningRequest.getDepot().getAddress() != null;
    }

    @Test
    public void planningRequestInvalidXmlMapper() {
        boolean exceptionThrown = false;
        XmlObjectMapper xmlObjectMapper = new XmlObjectMapper("src/main/resources/xml/invalidRequests.xml");
        try {
            xmlObjectMapper.planningRequestXmlMapper();
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }


}
