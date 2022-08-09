package insa.lyon.justif.utils.exceptions;

public class InvalidXMLException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidXMLException(String errorMessage) {
        super(errorMessage);
    }

}