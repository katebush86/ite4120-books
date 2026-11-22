package ee.taltech.ite4120.books.borrowerregistry;

/**
 * The port through which the books component asks "who is this borrower?".
 * The rest of the component depends only on this interface; which transport
 * answers is chosen by configuration — see {@link BorrowerRegistryConfig}.
 */
public interface BorrowerRegistryAdapter {

    /**
     * Look up a person by Estonian personal code.
     *
     * @return the person, or {@code null} when the registry does not know them
     * @throws BorrowerRegistryException when the registry cannot be reached —
     *         the caller decides whether that fails open or closed
     */
    BorrowerInfo lookup(String isikukood);
}
