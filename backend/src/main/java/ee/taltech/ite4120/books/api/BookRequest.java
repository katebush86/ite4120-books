package ee.taltech.ite4120.books.api;

import ee.taltech.ite4120.books.model.Book;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The register request body ({@code POST /api/books}). Bean Validation checks the
 * shape here; the rules that need context (genre list, page count, duplicate
 * ISBN, pre-order) live in BookService.
 */
public class BookRequest {

    /** 10 or 13 digits; hyphens and spaces are refused, not stripped. */
    @NotBlank
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "isbn must be 10 or 13 digits, without hyphens or spaces")
    private String isbn;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String author;

    @NotBlank
    private String genre;

    /** Must be greater than 0 — checked in BookService with the spec's exact message. */
    @NotNull
    private Integer pageCount;

    @NotNull
    @Min(value = 1000, message = "publicationYear must have 4 digits")
    @Max(value = 9999, message = "publicationYear must have 4 digits")
    private Integer publicationYear;

    /** Optional, default 1. */
    @Min(value = 1, message = "copyCount must be at least 1")
    @Max(value = 100, message = "copyCount must be at most 100")
    private Integer copyCount;

    public Book toModel() {
        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitle(title);
        b.setAuthor(author);
        b.setGenre(genre);
        b.setPageCount(pageCount);
        b.setPublicationYear(publicationYear);
        return b;
    }

    /** The number of copies to create: 1 when not given. */
    public int copyCountOrDefault() {
        return copyCount == null ? 1 : copyCount;
    }

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
    public Integer getCopyCount() { return copyCount; }
    public void setCopyCount(Integer copyCount) { this.copyCount = copyCount; }
}
