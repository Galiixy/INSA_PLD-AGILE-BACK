package insa.lyon.justif.service;

import insa.lyon.justif.generated.model.Intersection;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapServiceTest {

    private final MapService mapService = new MapService();

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void parseInvalidMapXmlTest() {
        boolean exceptionThrown = false;
        try {
            mapService.parseMapXml("src/main/resources/xml/invalidMap.xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    private void validateBuiltMap(StreetMap streetMap) {
        for (Intersection intersection : streetMap.getIntersections().values()) {
            assert intersection.getId() != null;
            assert intersection.getLatitude() != null;
            assert intersection.getLongitude() != null;
            assert intersection.getOutgoingSegments() != null;
        }
    }

    private void runParseValidMapTest(String mapSize) {
        boolean exceptionThrown = false;
        try {
            mapService.parseMapXml("src/main/resources/xml/" + mapSize + ".xml");
        } catch (ParsingXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
        StreetMap streetMap = mapService.getStreetMap();
        assert streetMap != null;
        validateBuiltMap(streetMap);
    }

    @Test
    public void parseValidSmallMapXmlTest() {
        runParseValidMapTest("smallMap");
    }

    @Test
    public void parseValidMediumMapXmlTest() {
        runParseValidMapTest("mediumMap");
    }

    @Test
    public void parseValidLargeMapXmlTest() {
        runParseValidMapTest("largeMap");
    }

    @Test
    public void isValidValidMapXmlTest() {
        boolean exceptionThrown = false;
        try {
            mapService.isValidMapXml("src/main/resources/xml/smallMap.xml");
        } catch (InvalidXMLException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
    }

    @Test
    public void isValidInvalidMapXmlTest() {
        boolean exceptionThrown = false;
        try {
            mapService.isValidMapXml("src/main/resources/xml/invalidMap.xml");
        } catch (InvalidXMLException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }

    @Test
    public void getValidIntersectionByIdTest() {
        try {
            mapService.parseMapXml("src/main/resources/xml/smallMap.xml");
        } catch (ParsingXMLException ignored) {
        }
        assert mapService.getIntersectionById("25175791") != null;
    }

    @Test
    public void getInvalidIntersectionByIdTest() {
        try {
            mapService.parseMapXml("src/main/resources/xml/smallMap.xml");
        } catch (ParsingXMLException ignored) {
        }
        assert mapService.getIntersectionById("1111111") == null;
    }

    @Test
    public void setMapBoundsTest() {
        try {
            mapService.parseMapXml("src/main/resources/xml/smallMap.xml");
        } catch (ParsingXMLException ignored) {
        }
        mapService.setMapBounds();
        assert mapService.getStreetMap().getLatitudeMax() != null;
        assert mapService.getStreetMap().getLongitudeMax() != null;
        assert mapService.getStreetMap().getLatitudeMin() != null;
        assert mapService.getStreetMap().getLongitudeMin() != null;
    }

    @Test
    public void isValidValidMapXmlFileNameTest() {
        boolean exceptionThrown = false;
        try {
            mapService.isValidMapXmlFileName("map.xml");
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = true;
        }
        assert !exceptionThrown;
    }

    @Test
    public void isValidInvalidMapXmlFileNameTest() {
        boolean exceptionThrown = false;
        try {
            mapService.isValidMapXmlFileName("map.test");
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = true;
        }
        assert exceptionThrown;
    }
}
