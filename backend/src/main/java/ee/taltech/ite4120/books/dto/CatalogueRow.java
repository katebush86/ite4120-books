package ee.taltech.ite4120.books.dto;

import java.time.LocalDate;

/**
 * One row of the catalogue: one physical copy together with its title's data.
 *
 * <p>{@code overdueDays} is computed in the query, never stored.
 * {@code borrowerName} comes from the borrower registry and is never stored;
 * it is {@code null} when the registry is down or does not know the person.
 */
public class CatalogueRow {

    private Long copyId;
    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private Integer pageCount;
    private Integer publicationYear;
    private String status;
    private String borrowerPersonalCode;
    private LocalDate dueDate;
    private Integer overdueDays;
    private String borrowerName;

    public Long getCopyId() { return copyId; }
    public void setCopyId(Long copyId) { this.copyId = copyId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBorrowerPersonalCode() { return borrowerPersonalCode; }
    public void setBorrowerPersonalCode(String borrowerPersonalCode) { this.borrowerPersonalCode = borrowerPersonalCode; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
}
