package ee.taltech.ite4120.books.repository;

import ee.taltech.ite4120.books.model.Copy;
import java.util.List;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.springframework.stereotype.Repository;

/** Data access for {@code books.copy}. Every query filters active rows. */
@Repository
public class CopyRepository extends BaseRepository {

    private static final String SELECT = "select c.* from books.copy c ";

    private final PgBeanProcessor bp = new PgBeanProcessor(Copy.class);

    public Copy load(Long id) {
        return getBean(SELECT + "where c.id = ? and c.sys_status = 'A'", bp, id);
    }

    public List<Copy> listByBook(Long bookId) {
        return getBeans(SELECT + "where c.book_id = ? and c.sys_status = 'A' order by c.id", bp, bookId);
    }

    public Long insert(Long bookId, String status) {
        return queryForObject(
                "insert into books.copy (book_id, status) values (?, ?) returning id",
                Long.class, bookId, status);
    }

    /** Replaces the three lending fields as a whole, as the spec's PUT says. */
    public boolean updateLending(Copy c) {
        String sql = """
                update books.copy
                   set status = ?, borrower_personal_code = ?, due_date = ?
                 where id = ? and sys_status = 'A'""";
        return jdbcTemplate.update(sql, c.getStatus(), c.getBorrowerPersonalCode(), c.getDueDate(), c.getId()) == 1;
    }

    /** How many active copies of a title are BORROWED or IN_QUEUE — the removal rule. */
    public int countLentOut(Long bookId) {
        Integer n = queryForObject("""
                select count(1) from books.copy
                 where book_id = ? and sys_status = 'A' and status in ('BORROWED', 'IN_QUEUE')""",
                Integer.class, bookId);
        return n == null ? 0 : n;
    }

    /** Soft delete of every copy of a title. */
    public void retireByBook(Long bookId) {
        jdbcTemplate.update(
                "update books.copy set sys_status = 'C' where book_id = ? and sys_status = 'A'", bookId);
    }
}
