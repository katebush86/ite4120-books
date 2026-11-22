package ee.taltech.ite4120.books;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ee.taltech.ite4120.books.dto.BookDetail;
import ee.taltech.ite4120.books.model.Book;
import ee.taltech.ite4120.books.model.Copy;
import ee.taltech.ite4120.books.service.BookService;
import java.time.LocalDate;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * THE BUSINESS TESTS of the books component — the executable form of the
 * plain-language tests in docs/src/specifications/BOOKS.01 § Business tests.
 * Each test names the spec test it implements; the expected messages are
 * quoted from the spec.
 *
 * <p>Runs against a real PostgreSQL via Testcontainers, like the animals tests:
 * the whole Liquibase changelog is applied to a throwaway database. Every test
 * uses its own ISBN, so the tests do not depend on each other or on the demo data.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class BookBusinessRulesIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    BookService service;

    /** Spec test 1: a future publication year means Pre-order. */
    @Test
    void aFuturePublicationYearMeansPreOrder() {
        // Given a title whose publication year is next year
        Book book = book("9789949100001");
        book.setPublicationYear(LocalDate.now().getYear() + 1);

        // When it is registered, then all its copies are PRE_ORDER
        BookDetail saved = service.create(book, 2);
        assertThat(saved.copies().size()).isEqualTo(2);
        assertThat(saved.copies().stream().allMatch(c -> "PRE_ORDER".equals(c.getStatus()))).isTrue();

        // And when one copy is set to AVAILABLE, the change is refused
        Long copyId = saved.copies().get(0).getId();
        assertThatThrownBy(() -> service.updateLending(copyId, "AVAILABLE", null, null))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("status must be PRE_ORDER while publication year is in the future");
    }

    /** Spec test 2: the page count must be positive. */
    @Test
    void thePageCountMustBePositive() {
        // Given a request with a page count of 0, when it is submitted, then it is refused
        Book zero = book("9789949100002");
        zero.setPageCount(0);
        assertThatThrownBy(() -> service.create(zero, 1))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("pageCount must be greater than 0");

        // And the same for a negative page count
        Book negative = book("9789949100002");
        negative.setPageCount(-5);
        assertThatThrownBy(() -> service.create(negative, 1))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("pageCount must be greater than 0");
    }

    /** Spec test 6: an ISBN identifies exactly one active book title. */
    @Test
    void anIsbnIdentifiesExactlyOneActiveTitle() {
        // Given a registered title with ISBN X
        BookDetail first = service.create(book("9789949100006"), 1);
        assertThat(first.book().getId()).isNotNull();

        // When a second title claims X, then the registration conflicts
        assertThatThrownBy(() -> service.create(book("9789949100006"), 1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");

        // And when the first title has been removed, X may be registered again (soft delete)
        service.retire(first.book().getId());
        BookDetail again = service.create(book("9789949100006"), 1);
        assertThat(again.book().getId()).isNotEqualTo(first.book().getId());
    }

    /** Spec test 10: a title with an active borrower cannot be removed. */
    @Test
    void aTitleWithABorrowedCopyCannotBeRemoved() {
        // Given a title with a BORROWED copy
        BookDetail saved = service.create(book("9789949100010"), 1);
        Long bookId = saved.book().getId();
        Copy copy = saved.copies().get(0);
        service.updateLending(copy.getId(), "BORROWED", "38102130265", null);

        // When it is removed, then the removal conflicts
        assertThatThrownBy(() -> service.retire(bookId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("BORROWED or IN_QUEUE");

        // And once the copy is returned, it can be removed and is gone
        service.updateLending(copy.getId(), "AVAILABLE", null, null);
        service.retire(bookId);
        assertThatThrownBy(() -> service.requireBook(bookId))
                .isInstanceOf(NotFoundException.class);
    }

    private static Book book(String isbn) {
        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitle("Test Book");
        b.setAuthor("Test Author");
        b.setGenre("FICTION");
        b.setPageCount(250);
        b.setPublicationYear(2001);
        return b;
    }
}
