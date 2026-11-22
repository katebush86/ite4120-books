package ee.taltech.ite4120.books.repository;

import ee.taltech.ite4120.books.dto.BookQueryParams;
import ee.taltech.ite4120.books.dto.CatalogueRow;
import ee.taltech.ite4120.books.model.Book;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.helex.commons.db.sql.SqlBuilder;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@code books.book} and for the catalogue (copies joined with
 * their titles). Same shape as AnimalRepository: BaseRepository + PgBeanProcessor
 * + SqlBuilder; every query filters active rows ({@code sys_status = 'A'}).
 */
@Repository
public class BookRepository extends BaseRepository {

    private static final String SELECT_BOOK = "select b.* from books.book b ";

    /** One row per active copy of an active title. Overdue days are computed here, never stored. */
    private static final String SELECT_CATALOGUE = """
            select c.id as copy_id, b.id as book_id, b.isbn, b.title, b.author, b.genre,
                   b.page_count, b.publication_year, c.status, c.borrower_personal_code, c.due_date,
                   case when c.status = 'BORROWED' and c.due_date < current_date
                        then current_date - c.due_date else 0 end as overdue_days
              from books.copy c
              join books.book b on b.id = c.book_id
             where c.sys_status = 'A' and b.sys_status = 'A'
            """;

    private static final String COUNT_CATALOGUE = """
            select count(1)
              from books.copy c
              join books.book b on b.id = c.book_id
             where c.sys_status = 'A' and b.sys_status = 'A'
            """;

    /** The sort keys the catalogue accepts (`sort=title`, `sort=-title`) and their columns. */
    private static final Map<String, String> SORT = Map.of(
            "title", "b.title",
            "author", "b.author",
            "isbn", "b.isbn",
            "publicationYear", "b.publication_year",
            "status", "c.status",
            "dueDate", "c.due_date");

    private final PgBeanProcessor bookBp = new PgBeanProcessor(Book.class);
    private final PgBeanProcessor rowBp = new PgBeanProcessor(CatalogueRow.class);

    public Book load(Long id) {
        return getBean(SELECT_BOOK + "where b.id = ? and b.sys_status = 'A'", bookBp, id);
    }

    public Book loadByIsbn(String isbn) {
        return getBean(SELECT_BOOK + "where b.isbn = ? and b.sys_status = 'A'", bookBp, isbn);
    }

    /**
     * The catalogue, paged. {@code enrich} is applied to every row of the page —
     * the service uses it to add the borrower's name from the registry.
     */
    public QueryResult<CatalogueRow> search(BookQueryParams p, Consumer<CatalogueRow> enrich) {
        return query(p, this::count, params -> {
            List<CatalogueRow> rows = list(params);
            rows.forEach(enrich);
            return rows;
        });
    }

    /** One catalogue row by copy id, or null. */
    public CatalogueRow loadRow(Long copyId) {
        return getBean(SELECT_CATALOGUE + " and c.id = ?", rowBp, copyId);
    }

    private Integer count(BookQueryParams p) {
        SqlBuilder sb = new SqlBuilder(COUNT_CATALOGUE);
        sb.append(filter(p));
        return queryForObject(sb.getSql(), Integer.class, sb.getParams());
    }

    private List<CatalogueRow> list(BookQueryParams p) {
        SqlBuilder sb = new SqlBuilder(SELECT_CATALOGUE);
        sb.append(filter(p));
        sb.append(order(p));
        sb.append(limit(p));
        return getBeans(sb.getSql(), rowBp, sb.getParams());
    }

    /** A mistyped {@code ?sort=} is the caller's fault: 400 with the allowed keys, not 500. */
    private SqlBuilder order(BookQueryParams p) {
        try {
            return order(p, SORT);
        } catch (IllegalArgumentException e) {
            throw new ApiClientException("unknown sort key; sortable: " + String.join(", ", SORT.keySet().stream().sorted().toList()));
        }
    }

    private SqlBuilder filter(BookQueryParams p) {
        SqlBuilder sb = new SqlBuilder();
        if (p.getTextContains() != null && !p.getTextContains().isBlank()) {
            String like = "%" + p.getTextContains().toLowerCase() + "%";
            sb.and("(b.isbn like ? or lower(b.title) like ? or lower(b.author) like ?)", like, like, like);
        }
        sb.appendIfNotNull("and b.genre = ?", p.getGenre());
        sb.appendIfNotNull("and c.status = ?", p.getStatus());
        return sb;
    }

    public Long insert(Book b) {
        String sql = """
                insert into books.book (isbn, title, author, genre, page_count, publication_year)
                values (?, ?, ?, ?, ?, ?)
                returning id""";
        return queryForObject(sql, Long.class,
                b.getIsbn(), b.getTitle(), b.getAuthor(), b.getGenre(), b.getPageCount(), b.getPublicationYear());
    }

    /** Soft delete — rows are never hard-deleted. */
    public boolean retire(Long id) {
        return jdbcTemplate.update(
                "update books.book set sys_status = 'C' where id = ? and sys_status = 'A'", id) == 1;
    }
}
