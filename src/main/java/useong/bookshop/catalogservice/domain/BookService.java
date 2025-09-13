package useong.bookshop.catalogservice.domain;

import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

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
              .createdAt(existingBook.getCreatedAt())
              .updatedAt(existingBook.getUpdatedAt())
              .version(existingBook.getVersion())
              .build();

          return bookRepository.save(bookToUpdate);
        })
        .orElseGet(() -> addBookToCatalog(book));
  }

}
