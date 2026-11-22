package ee.taltech.ite4120.books.borrowerregistry;

/** The borrower registry could not answer — transport failure or upstream fault. */
public class BorrowerRegistryException extends RuntimeException {

    public BorrowerRegistryException(String message) {
        super(message);
    }

    public BorrowerRegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
