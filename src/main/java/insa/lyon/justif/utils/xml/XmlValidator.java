package insa.lyon.justif.utils.xml;

import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * XmlValidator allows to validate an XML file with two methods :
 * - Check that the extension of the file is ".xml"
 * - Check that the XML file verify the corresponding XSD file.
 *
 */
public class XmlValidator {

    private String xmlPath;

    private InputStream xsdPath;

    public XmlValidator() { }


    public XmlValidator(String xmlPath, InputStream xsdPath) {
        this.xmlPath = xmlPath;
        this.xsdPath = xsdPath;
    }

    public XmlValidator(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public void setXsdPath(InputStream xsdPath) {
        this.xsdPath = xsdPath;
    }

    /**
     * validateXmlPath : This method allows to check that file to validate has an XML extension.
     */
    public void validateXmlPath() throws InvalidFileExtensionException {
        if (!xmlPath.endsWith(".xml")) throw new InvalidFileExtensionException("The provided file isn't an XML file.");
    }

    /**
     * validateXmlWithXSD : This method allows to validate an XML file thanks to the corresponding XSD file.
     * The XSD file depends on the type of the XML file (map or planning request).
     */
    public void validateXmlWithXSD() throws InvalidXMLException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Schema schema = factory.newSchema(new StreamSource(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            throw new InvalidXMLException(e.getMessage());
        }
    }

}
