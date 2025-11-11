package com.polarbookshop.domain;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Iterable<Book> viewBookList() {
    return bookRepository.findAll();
  }

  public Book viewBookDetails(String isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new BookNotFoundException(isbn));
  }

  public Book addBookToCatalog(Book book) {
    if (bookRepository.existsByIsbn(book.getIsbn())) {
      throw new BookAlreadyExistsException(book.getIsbn());
    }
    return bookRepository.save(book);
  }

  @Transactional
  public void removeBookFromCatalog(String isbn) {
    bookRepository.deleteByIsbn(isbn);
  }

  public Book editBookDetails(String isbn, Book book) {
    return bookRepository.findByIsbn(isbn)
        .map(existingBook -> {
          Book bookToUpdate = Book.builder()
              .id(existingBook.getId())
              .isbn(existingBook.getIsbn())
              .title(book.getTitle())
              .author(book.getAuthor())
              .price(book.getPrice())
              .publisher(book.getPublisher())
              .version(existingBook.getVersion())
              .createdAt(existingBook.getCreatedAt())
              .updatedAt(existingBook.getUpdatedAt())
              .createdBy(existingBook.getCreatedBy())
              .lastModifiedBy(existingBook.getLastModifiedBy())
              .build();

          return bookRepository.save(bookToUpdate);
        })
        .orElseGet(() -> addBookToCatalog(book));
  }

}
