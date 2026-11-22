package ee.taltech.ite4120.books.service;

import static ee.taltech.ite4120.books.model.BookCodes.BORROWED;
import static ee.taltech.ite4120.books.model.BookCodes.GENRES;
import static ee.taltech.ite4120.books.model.BookCodes.LENDING_DAYS;
import static ee.taltech.ite4120.books.model.BookCodes.PRE_ORDER;
import static ee.taltech.ite4120.books.model.BookCodes.STATUSES;
import static ee.taltech.ite4120.books.model.BookCodes.WITH_BORROWER;

import ee.taltech.ite4120.books.borrowerregistry.BorrowerInfo;
import ee.taltech.ite4120.books.borrowerregistry.BorrowerRegistryAdapter;
import ee.taltech.ite4120.books.borrowerregistry.BorrowerRegistryException;
import ee.taltech.ite4120.books.dto.BookDetail;
import ee.taltech.ite4120.books.dto.BookQueryParams;
import ee.taltech.ite4120.books.dto.CatalogueRow;
import ee.taltech.ite4120.books.model.Book;
import ee.taltech.ite4120.books.model.BookCodes;
import ee.taltech.ite4120.books.model.Copy;
import ee.taltech.ite4120.books.repository.BookRepository;
import ee.taltech.ite4120.books.repository.CopyRepository;
import java.time.LocalDate;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business rules of the books component — exactly the rules in
 * docs/src/specifications/BOOKS.01-book-register.md. Each rule below names the
 * business test that checks it.
 */
@Service
public class BookService {

    private final BookRepository books;
    private final CopyRepository copies;
    private final BorrowerRegistryAdapter borrowerRegistry;

    public BookService(BookRepository books, CopyRepository copies, BorrowerRegistryAdapter borrowerRegistry) {
        this.books = books;
        this.copies = copies;
        this.borrowerRegistry = borrowerRegistry;
    }

    // ---- reading -----------------------------------------------------------

    /** The catalogue, one row per copy (tests 3, 9, 11, 12). */
    public QueryResult<CatalogueRow> search(BookQueryParams params) {
        if (params.getGenre() != null && !GENRES.contains(params.getGenre())) {
            throw new ApiClientException("unknown genre: " + params.getGenre());
        }
        if (params.getStatus() != null && !STATUSES.contains(params.getStatus())) {
            throw new ApiClientException("unknown status: " + params.getStatus());
        }
        return books.search(params, this::addBorrowerName);
    }

    public BookDetail requireBook(Long id) {
        Book book = books.load(id);
        if (book == null) {
            throw new NotFoundException("book " + id + " not found");
        }
        return new BookDetail(book, copies.listByBook(id));
    }

    public CatalogueRow requireCopyRow(Long copyId) {
        CatalogueRow row = books.loadRow(copyId);
        if (row == null) {
            throw new NotFoundException("copy " + copyId + " not found");
        }
        addBorrowerName(row);
        return row;
    }

    /**
     * Fail open (test 12): the borrower's name is auxiliary. When the registry is
     * down or does not know the person, the name stays null and the row is
     * returned normally.
     */
    private void addBorrowerName(CatalogueRow row) {
        if (row.getBorrowerPersonalCode() == null) {
            return;
        }
        try {
            BorrowerInfo person = borrowerRegistry.lookup(row.getBorrowerPersonalCode());
            row.setBorrowerName(person == null ? null : person.fullName());
        } catch (BorrowerRegistryException e) {
            row.setBorrowerName(null);
        }
    }

    // ---- registering -------------------------------------------------------

    /** Registers a title and creates its copies (tests 1, 2, 6, 8). */
    @Transactional
    public BookDetail create(Book book, int copyCount) {
        // Test 8: the genre must be on the list.
        if (!GENRES.contains(book.getGenre())) {
            throw new ApiClientException("unknown genre: " + book.getGenre());
        }
        // Test 2: the page count must be positive.
        if (book.getPageCount() <= 0) {
            throw new ApiClientException("pageCount must be greater than 0");
        }
        // Test 6: an ISBN identifies exactly one ACTIVE title.
        if (books.loadByIsbn(book.getIsbn()) != null) {
            throw new ConflictException("book",
                    "an active book with ISBN " + book.getIsbn() + " already exists");
        }
        Long id = books.insert(book);
        // Test 1 / default status: a future publication year means Pre-order.
        String status = isFutureYear(book) ? PRE_ORDER : BookCodes.AVAILABLE;
        for (int i = 0; i < copyCount; i++) {
            copies.insert(id, status);
        }
        return requireBook(id);
    }

    // ---- lending -----------------------------------------------------------

    /** Sets status, borrower and due date of one copy, replaced as a whole (tests 1, 4, 5, 7). */
    @Transactional
    public CatalogueRow updateLending(Long copyId, String status, String borrowerPersonalCode, LocalDate dueDate) {
        Copy copy = copies.load(copyId);
        if (copy == null) {
            throw new NotFoundException("copy " + copyId + " not found");
        }
        if (!STATUSES.contains(status)) {
            throw new ApiClientException("unknown status: " + status);
        }
        // Test 1: while the publication year is in the future, every copy stays PRE_ORDER.
        Book book = books.load(copy.getBookId());
        if (book != null && isFutureYear(book) && !PRE_ORDER.equals(status)) {
            throw new ApiClientException("status must be PRE_ORDER while publication year is in the future");
        }
        if (WITH_BORROWER.contains(status)) {
            // Test 5: a BORROWED (or IN_QUEUE) copy has a borrower.
            if (borrowerPersonalCode == null || borrowerPersonalCode.isBlank()) {
                throw new ApiClientException("borrowerPersonalCode is required for status " + status);
            }
        } else if (borrowerPersonalCode != null && !borrowerPersonalCode.isBlank()) {
            // Test 4: copies that are not out have no borrower.
            throw new ApiClientException("borrower not allowed for this status");
        }
        if (BORROWED.equals(status)) {
            // Test 5: a BORROWED copy with no due date gets today + 14 days.
            if (dueDate == null) {
                dueDate = LocalDate.now().plusDays(LENDING_DAYS);
            }
        } else if (dueDate != null) {
            throw new ApiClientException("dueDate not allowed for this status");
        }

        copy.setStatus(status);
        copy.setBorrowerPersonalCode(WITH_BORROWER.contains(status) ? borrowerPersonalCode : null);
        copy.setDueDate(dueDate);
        if (!copies.updateLending(copy)) {
            throw new NotFoundException("copy " + copyId + " not found");
        }
        return requireCopyRow(copyId);
    }

    // ---- removing ----------------------------------------------------------

    /** Removes a title and all its copies — soft delete (tests 6, 10). */
    @Transactional
    public void retire(Long id) {
        if (books.load(id) == null) {
            throw new NotFoundException("book " + id + " not found");
        }
        // Test 10: not while a copy is lent out or held for someone.
        if (copies.countLentOut(id) > 0) {
            throw new ConflictException("book",
                    "book " + id + " cannot be removed while a copy is BORROWED or IN_QUEUE");
        }
        copies.retireByBook(id);
        books.retire(id);
    }

    private static boolean isFutureYear(Book book) {
        return book.getPublicationYear() > LocalDate.now().getYear();
    }
}
