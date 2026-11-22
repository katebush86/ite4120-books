package ee.taltech.ite4120.books.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * One physical copy of a book title, with its current lending state.
 * Maps 1:1 onto {@code books.copy} — see 02-copy.sql.
 */
public class Copy {

    private Long id;
    /** FK → books.book(id). */
    private Long bookId;
    /** One of {@link BookCodes#STATUSES}. */
    private String status;
    /** Estonian personal code of the borrower. A STRING, never a number. */
    private String borrowerPersonalCode;
    private LocalDate dueDate;

    private String sysStatus;
    private Long sysVersion;
    private OffsetDateTime sysCreatedAt;
    private String sysCreatedBy;
    private OffsetDateTime sysModifiedAt;
    private String sysModifiedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBorrowerPersonalCode() { return borrowerPersonalCode; }
    public void setBorrowerPersonalCode(String borrowerPersonalCode) { this.borrowerPersonalCode = borrowerPersonalCode; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getSysStatus() { return sysStatus; }
    public void setSysStatus(String sysStatus) { this.sysStatus = sysStatus; }
    public Long getSysVersion() { return sysVersion; }
    public void setSysVersion(Long sysVersion) { this.sysVersion = sysVersion; }
    public OffsetDateTime getSysCreatedAt() { return sysCreatedAt; }
    public void setSysCreatedAt(OffsetDateTime sysCreatedAt) { this.sysCreatedAt = sysCreatedAt; }
    public String getSysCreatedBy() { return sysCreatedBy; }
    public void setSysCreatedBy(String sysCreatedBy) { this.sysCreatedBy = sysCreatedBy; }
    public OffsetDateTime getSysModifiedAt() { return sysModifiedAt; }
    public void setSysModifiedAt(OffsetDateTime sysModifiedAt) { this.sysModifiedAt = sysModifiedAt; }
    public String getSysModifiedBy() { return sysModifiedBy; }
    public void setSysModifiedBy(String sysModifiedBy) { this.sysModifiedBy = sysModifiedBy; }
}
