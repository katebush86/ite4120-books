package ee.taltech.ite4120.books.api;

import ee.taltech.ite4120.books.dto.BookDetail;
import ee.taltech.ite4120.books.dto.BookQueryParams;
import ee.taltech.ite4120.books.dto.CatalogueRow;
import ee.taltech.ite4120.books.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.helex.commons.model.QueryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The books REST API: the catalogue, registering and removing book titles. */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Library book catalogue — titles and their copies")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Operation(summary = "The catalogue: one row per copy — paginated, searchable, filterable by genre and status")
    @ApiResponse(responseCode = "200", description = "One page of catalogue rows")
    @ApiResponse(responseCode = "400", description = "Unknown genre, status or sort key")
    @GetMapping
    public QueryResult<CatalogueRow> search(@ModelAttribute BookQueryParams params) {
        return service.search(params);
    }

    @Operation(summary = "One book title with its copies")
    @ApiResponse(responseCode = "200", description = "The title and its active copies")
    @ApiResponse(responseCode = "404", description = "No active book with that id")
    @GetMapping("/{id}")
    public BookDetail byId(@PathVariable Long id) {
        return service.requireBook(id);
    }

    @Operation(summary = "Register a book title and create its copies")
    @ApiResponse(responseCode = "201", description = "Created — Location header points at the new book")
    @ApiResponse(responseCode = "400", description = "Validation failed (ISBN format, page count, unknown genre…)")
    @ApiResponse(responseCode = "409", description = "ISBN already in use by an active book")
    @PostMapping
    public ResponseEntity<BookDetail> create(@Valid @RequestBody BookRequest body) {
        BookDetail saved = service.create(body.toModel(), body.copyCountOrDefault());
        return ResponseEntity.created(URI.create("/api/books/" + saved.book().getId())).body(saved);
    }

    @Operation(summary = "Remove a book title and all its copies (soft delete)")
    @ApiResponse(responseCode = "204", description = "Removed")
    @ApiResponse(responseCode = "404", description = "No active book with that id")
    @ApiResponse(responseCode = "409", description = "A copy is BORROWED or IN_QUEUE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retire(@PathVariable Long id) {
        service.retire(id);
        return ResponseEntity.noContent().build();
    }
}
