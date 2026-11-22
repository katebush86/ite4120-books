package ee.taltech.ite4120.books.borrowerregistry;

/**
 * A borrower, in OUR vocabulary — English names, our shape. The registry's own
 * (Estonian) field names stay in the wire record and are mapped in one place.
 */
public record BorrowerInfo(String isikukood, String firstName, String lastName) {

    /** "First Last", as the catalogue shows it. */
    public String fullName() {
        return firstName + " " + lastName;
    }
}
