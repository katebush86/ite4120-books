package ee.taltech.ite4120.books.model;

import java.util.Set;

/**
 * The two fixed lists from the specification. They are enforced by check
 * constraints in the database (01-book.sql, 02-copy.sql) and checked here first,
 * so a wrong value answers a clear 400 instead of a raw database error.
 */
public final class BookCodes {

    public static final Set<String> GENRES = Set.of(
            "FICTION", "NON_FICTION", "FANTASY", "SCIENCE_FICTION", "MYSTERY",
            "BIOGRAPHY", "HISTORY", "CHILDREN", "POETRY", "OTHER");

    public static final String AVAILABLE = "AVAILABLE";
    public static final String BORROWED = "BORROWED";
    public static final String IN_QUEUE = "IN_QUEUE";
    public static final String UNDER_REPAIR = "UNDER_REPAIR";
    public static final String PRE_ORDER = "PRE_ORDER";
    public static final String LOST = "LOST";

    public static final Set<String> STATUSES = Set.of(
            AVAILABLE, BORROWED, IN_QUEUE, UNDER_REPAIR, PRE_ORDER, LOST);

    /** Statuses that carry a borrower: the copy is lent out or held for someone. */
    public static final Set<String> WITH_BORROWER = Set.of(BORROWED, IN_QUEUE);

    /** Default lending period in days. */
    public static final int LENDING_DAYS = 14;

    private BookCodes() {}
}
