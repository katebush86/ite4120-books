package ee.taltech.ite4120.books.dto;

import org.helex.commons.model.QueryParams;

/**
 * Query parameters for the catalogue endpoint. Extends the platform
 * {@link QueryParams}, which carries {@code limit}, {@code offset} and {@code sort}.
 */
public class BookQueryParams extends QueryParams {

    /** Case-insensitive substring match on ISBN, title or author. */
    private String textContains;
    /** Exact genre filter, e.g. {@code FANTASY}. */
    private String genre;
    /** Exact copy status filter, e.g. {@code BORROWED}. */
    private String status;

    public String getTextContains() { return textContains; }
    public void setTextContains(String textContains) { this.textContains = textContains; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
