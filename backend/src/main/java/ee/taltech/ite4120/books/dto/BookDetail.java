package ee.taltech.ite4120.books.dto;

import ee.taltech.ite4120.books.model.Book;
import ee.taltech.ite4120.books.model.Copy;
import java.util.List;

/** One book title with all its active copies — the answer of {@code GET /api/books/{id}}. */
public record BookDetail(Book book, List<Copy> copies) {}
