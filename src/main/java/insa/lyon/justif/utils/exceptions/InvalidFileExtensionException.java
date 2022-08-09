package insa.lyon.justif.utils.exceptions;

public class InvalidFileExtensionException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidFileExtensionException(String errorMessage) {
        super(errorMessage);
    }

}