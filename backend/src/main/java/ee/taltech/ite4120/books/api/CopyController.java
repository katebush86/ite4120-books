package ee.taltech.ite4120.books.api;

import ee.taltech.ite4120.books.dto.CatalogueRow;
import ee.taltech.ite4120.books.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The copies REST API: reading and updating the lending data of one physical copy. */
@RestController
@RequestMapping("/api/copies")
@Tag(name = "Copies", description = "Physical copies and their lending state")
public class CopyController {

    private final BookService service;

    public CopyController(BookService service) {
        this.service = service;
    }

    @Operation(summary = "One copy as a catalogue row — what the lending form shows")
    @ApiResponse(responseCode = "200", description = "The copy with its title data")
    @ApiResponse(responseCode = "404", description = "No active copy with that id")
    @GetMapping("/{id}")
    public CatalogueRow byId(@PathVariable Long id) {
        return service.requireCopyRow(id);
    }

    @Operation(summary = "Set the copy's lending data: status, borrower and due date, replaced as a whole")
    @ApiResponse(responseCode = "200", description = "The updated copy")
    @ApiResponse(responseCode = "400", description = "Not allowed for this status, malformed personal code…")
    @ApiResponse(responseCode = "404", description = "No active copy with that id")
    @PutMapping("/{id}")
    public CatalogueRow updateLending(@PathVariable Long id, @Valid @RequestBody CopyLendingRequest body) {
        return service.updateLending(id, body.getStatus(), body.getBorrowerPersonalCode(), body.getDueDate());
    }
}
