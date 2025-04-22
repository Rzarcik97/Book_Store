package bookstore.exeptions;

public class SpecificationSearchFailedException extends RuntimeException {
    public SpecificationSearchFailedException(String message) {
        super(message);
    }
}
