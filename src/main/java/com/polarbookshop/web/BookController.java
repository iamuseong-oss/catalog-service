package com.polarbookshop.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.polarbookshop.domain.Book;
import com.polarbookshop.domain.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  private static final Logger log = LoggerFactory.getLogger(BookController.class);

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Iterable<Book> get() {
    log.info("Fetching the list of books in the catalog");
    return bookService.viewBookList();
  }

  @GetMapping("{isbn}")
  @ResponseStatus(HttpStatus.OK)
  public Book getByIsbn(@PathVariable("isbn") String isbn) {
    log.info("Fetching the book with ISBN {} from the catalog", isbn);
    return bookService.viewBookDetails(isbn);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book post(@Valid @RequestBody Book book) {
    log.info("Adding a new book to the catalog with ISBN {}", book.getIsbn());
    return bookService.addBookToCatalog(book);
  }

  @DeleteMapping("{isbn}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("isbn") String isbn) {
    log.info("Deleting book with ISBN {}", isbn);
    bookService.removeBookFromCatalog(isbn);
  }

  @PutMapping("{isbn}")
  public Book put(@PathVariable("isbn") String isbn, @Valid @RequestBody Book book) {
    log.info("Updating book with ISBN {}", isbn);
    return bookService.editBookDetails(isbn, book);
  }

}
