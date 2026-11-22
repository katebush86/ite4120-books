package ee.taltech.ite4120.books.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * The lending request body ({@code PUT /api/copies/{id}}). The three fields
 * replace the copy's lending data as a whole. Which combinations are allowed
 * per status is checked in BookService.
 */
public class CopyLendingRequest {

    @NotBlank
    private String status;

    /** 11 digits — and a string, never a number. Only for BORROWED and IN_QUEUE. */
    @Pattern(regexp = "\\d{11}", message = "borrowerPersonalCode must be exactly 11 digits")
    private String borrowerPersonalCode;

    /** Only for BORROWED; when missing, today + 14 days. */
    private LocalDate dueDate;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBorrowerPersonalCode() { return borrowerPersonalCode; }
    public void setBorrowerPersonalCode(String borrowerPersonalCode) { this.borrowerPersonalCode = borrowerPersonalCode; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
