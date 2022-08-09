package insa.lyon.justif.unit;

import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.xml.XmlValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
public class XmlValidatorTest {


    private final XmlValidator xmlValidator = new XmlValidator();

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validateValidXmlPathTest() {
        xmlValidator.setXmlPath("test.xml");
        boolean exceptionThrown = true;
        try {
            xmlValidator.validateXmlPath();
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = false;
        }
        assert exceptionThrown;
    }

    @Test
    public void validateInvalidXmlPathTest() {
        xmlValidator.setXmlPath("test.png");
        boolean exceptionThrown = true;
        try {
            xmlValidator.validateXmlPath();
        } catch (InvalidFileExtensionException ex) {
            exceptionThrown = false;
        }
        assert !exceptionThrown;
    }

    @Test
    public void validateValidMapXmlWithXSDTest() {
        boolean exceptionThrown = true;
        try {
            xmlValidator.setXmlPath("src/main/resources/xml/smallMap.xml");
            xmlValidator.setXsdPath(new FileInputStream("src/main/resources/xsd/map.xsd"));
            xmlValidator.validateXmlWithXSD();
        } catch (InvalidXMLException | FileNotFoundException ex) {
            exceptionThrown = false;
        }
        assert exceptionThrown;
    }

    @Test
    public void validateInvalidMapXmlWithXSDTest() {
        boolean exceptionThrown = true;
        try {
            xmlValidator.setXmlPath("src/main/resources/xml/invalidMap.xml");
            xmlValidator.setXsdPath(new FileInputStream("src/main/resources/xsd/map.xsd"));
            xmlValidator.validateXmlWithXSD();
        } catch (InvalidXMLException | FileNotFoundException ex) {
            exceptionThrown = false;
        }
        assert !exceptionThrown;
    }

    @Test
    public void validateValidPlanningXmlWithXSDTest() {
        boolean isValid = true;
        try {
            xmlValidator.setXmlPath("src/main/resources/xml/requestsSmall1.xml");
            xmlValidator.setXsdPath(new FileInputStream("src/main/resources/xsd/planning.xsd"));
            xmlValidator.validateXmlWithXSD();
        } catch (InvalidXMLException | FileNotFoundException ex) {
            isValid = false;
        }
        assert isValid;
    }

    @Test
    public void validateInvalidPlanningXmlWithXSDTest() {
        boolean isValid = true;
        try {
            xmlValidator.setXmlPath("src/main/resources/xml/invalidRequests.xml");
            xmlValidator.setXsdPath(new FileInputStream("src/main/resources/xsd/planning.xsd"));
            xmlValidator.validateXmlWithXSD();
        } catch (InvalidXMLException | FileNotFoundException ex) {
            isValid = false;
        }
        assert !isValid;
    }
}
