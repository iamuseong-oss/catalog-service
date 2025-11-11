package com.polarbookshop.domain;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("test")
public class BookDataLoader {

  private final BookRepository bookRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void loadBookTestData() {
    bookRepository.deleteAll();
    var book1 = Book.builder()
        .isbn("1234567891")
        .title("Test Book 1")
        .author("Test Author 1")
        .price(1.00)
        .build();

    var book2 = Book.builder()
        .isbn("1234567892")
        .title("Test Book 2")
        .author("Test Author 2")
        .price(2.00)
        .build();

    bookRepository.saveAll(List.of(book1, book2));
  }

}
