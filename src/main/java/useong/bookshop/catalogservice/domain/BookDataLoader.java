package useong.bookshop.catalogservice.domain;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("testdata")
public class BookDataLoader {

  private final BookRepository bookRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void loadBookTestData() {
    bookRepository.deleteAll();
    var book1 = Book.builder()
        .isbn("1234567891")
        .title("Northern Lights")
        .author("Iorek Polarson")
        .price(9.90)
        .build();
    var book2 = Book.builder()
        .isbn("1234567892")
        .title("Polar Journey")
        .author("Iorek Polarson")
        .price(12.90)
        .build();
    bookRepository.saveAll(List.of(book1, book2));
  }
}
