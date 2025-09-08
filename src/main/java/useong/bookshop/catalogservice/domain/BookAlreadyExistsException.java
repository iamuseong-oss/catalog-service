package useong.bookshop.catalogservice.domain;

import lombok.Getter;

@Getter
public class BookAlreadyExistsException extends RuntimeException {

  public BookAlreadyExistsException(String isbn) {
    super("A book with ISBN " + isbn + " already exists.");
  }

}
