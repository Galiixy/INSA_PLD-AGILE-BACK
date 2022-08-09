package insa.lyon.justif.service;

import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.generated.model.Request;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidPlanningScopeException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlanningServiceTest {

    private final MapService mapService = new MapService();

    @InjectMocks
    private final PlanningService planningService = new PlanningService();

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        try {
            mapService.parseMapXml("src/main/resources/xml/smallMap.xml");
        } catch (ParsingXMLException ignored) {
        }
    }

    @Test
    public void parseInvalidPlanningXmlTest() {
        boolean exceptionThrown = false;
        try {
            planningService.parsePlanningRequestXml("src/main/resources/xml/invalidRequests.xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    private void validateBuiltPlanningRequest(PlanningRequest planningRequest) {
        for (Request request : planningRequest.getRequests()) {
            assert request.getPickupAddress() != null;
            assert request.getPickupDuration() != null;
            assert request.getDeliveryAddress() != null;
            assert request.getDeliveryDuration() != null;
        }

        assert planningRequest.getDepot().getAddress() != null;
        assert planningRequest.getDepot().getDepartureTime() != null;

    }

    @Test
    public void parseValidSmallPlanningXmlTest() {
        boolean exceptionThrown = false;
        try {
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsSmall2.xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        PlanningRequest planningRequest = planningService.getPlanningRequest();
        assert planningRequest != null;
        validateBuiltPlanningRequest(planningRequest);
    }

    @Test
    public void parseValidMediumPlanningXmlTest() {
        boolean exceptionThrown = false;
        try {
            mapService.parseMapXml("src/main/resources/xml/mediumMap.xml");
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsMedium5.xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        PlanningRequest planningRequest = planningService.getPlanningRequest();
        assert planningRequest != null;
        validateBuiltPlanningRequest(planningRequest);
    }

    @Test
    public void parseValidLargePlanningXmlTest() {
        boolean exceptionThrown = false;
        try {
            mapService.parseMapXml("src/main/resources/xml/largeMap.xml");
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsLarge9.xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        PlanningRequest planningRequest = planningService.getPlanningRequest();
        assert planningRequest != null;
        validateBuiltPlanningRequest(planningRequest);
    }

    @Test
    public void isValidValidMapXmlTest() {
        boolean exceptionThrown = false;
        try {
            planningService.isValidPlanningXml("src/main/resources/xml/requestsSmall1.xml");
        } catch (InvalidXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
    }

    @Test
    public void isValidInvalidMapXmlTest() {
        boolean exceptionThrown = false;
        try {
            planningService.isValidPlanningXml("src/main/resources/xml/invalidRequests.xml");
        } catch (InvalidXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    @Test
    public void isValidValidMapXmlFileNameTest() {
        boolean exceptionThrown = false;
        try {
            planningService.isValidPlanningXmlFileName("planning.xml");
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
    }

    @Test
    public void isValidInvalidMapXmlFileNameTest() {
        boolean exceptionThrown = false;
        try {
            planningService.isValidPlanningXmlFileName("planning.test");
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    @Test
    public void isValidValidScopeTest() {
        boolean exceptionThrown = false;
        try {
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsSmall1.xml");
            planningService.isValidPlanningScope(mapService.getStreetMap());
        } catch (ParsingXMLException ignored) {
        } catch (InvalidPlanningScopeException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
    }

    @Test
    public void isValidInvalidScopeTest() {
        boolean exceptionThrown = false;
        try {
            planningService.parsePlanningRequestXml("src/main/resources/xml/requestsLarge7.xml");
            planningService.isValidPlanningScope(mapService.getStreetMap());
        } catch (ParsingXMLException ignored) {
        } catch (InvalidPlanningScopeException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

}
