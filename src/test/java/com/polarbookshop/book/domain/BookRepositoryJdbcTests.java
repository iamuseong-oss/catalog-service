package com.polarbookshop.book.domain;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import com.polarbookshop.config.DataConfig;
import com.polarbookshop.domain.Book;
import com.polarbookshop.domain.BookRepository;

@DataJpaTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class BookRepositoryJdbcTests {

  @Autowired
  private BookRepository bookRepository;

  @AfterEach
  public void cleanup() {
    bookRepository.deleteAll();
  }

  @Test
  public void findAllBooks() {
    var book1 = Book.builder()
        .isbn("1234561235")
        .title("Title1")
        .author("Author1")
        .price(12.90)
        .publisher("Polarsophia")
        .build();

    var book2 = Book.builder()
        .isbn("1234561236")
        .title("Title2")
        .author("Author2")
        .price(12.90)
        .publisher("Polarsophia")
        .build();

    bookRepository.save(book1);
    bookRepository.save(book2);

    Iterable<Book> actualBooks = bookRepository.findAll();

    Assertions.assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
        .filter(book -> book.getIsbn().equals(book1.getIsbn()) || book.getIsbn().equals(book2.getIsbn()))
        .collect(Collectors.toList())).hasSize(2);
  }

  @Test
  public void whenCreateBookAuthenticatedThenNoAuditMetadata() {
    var bookToCreate = Book.builder()
        .isbn("1234567891")
        .title("Title")
        .author("Author")
        .price(12.90)
        .publisher("Polarsophia")
        .build();

    var createdBook = bookRepository.save(bookToCreate);

    Assertions.assertThat(createdBook.getCreatedBy()).isNull();
    Assertions.assertThat(createdBook.getLastModifiedBy()).isNull();
  }

  @Test
  @WithMockUser("john")
  public void whenCreateBookAuthenticatedThenAuditMetadata() {
    var bookToCreate = Book.builder()
        .isbn("1234567891")
        .title("Title")
        .author("Author")
        .price(12.90)
        .publisher("Polarsophia")
        .build();

    var createdBook = bookRepository.save(bookToCreate);

    Assertions.assertThat(createdBook.getCreatedBy()).isEqualTo("john");
    Assertions.assertThat(createdBook.getLastModifiedBy()).isEqualTo("john");
  }

}
