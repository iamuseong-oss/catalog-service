package com.polarbookshop.book.domain;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polarbookshop.domain.Book;

@JsonTest
@Profile("test")
public class BookJsonTests {

  private JacksonTester<Book> json;

  @BeforeEach
  void setup() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);
  }

  @Test
  public void testSerialize() throws IOException {
    Book book = Book.builder()
        .isbn("1234567891")
        .title("Test Book 1")
        .author("Test Author 1")
        .price(1.00)
        .build();

    JsonContent<Book> jsonContent = json.write(book);
    String jsonString = jsonContent.getJson();

    Assertions.assertTrue(jsonString.contains("\"isbn\":\"1234567891\""));
    Assertions.assertTrue(jsonString.contains("\"title\":\"Test Book 1\""));
    Assertions.assertTrue(jsonString.contains("\"author\":\"Test Author 1\""));
    Assertions.assertTrue(jsonString.contains("\"price\":1.0"));
  }

}
