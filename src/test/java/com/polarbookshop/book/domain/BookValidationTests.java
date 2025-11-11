package com.polarbookshop.book.domain;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.polarbookshop.domain.Book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class BookValidationTests {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void whenAllFieldsCorrectThenValidationSucceeds() {
    Book book = Book.builder()
        .isbn("1234567891")
        .title("Test Book 1")
        .author("Test Author 1")
        .price(1.00)
        .build();

    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    Assertions.assertTrue(violations.isEmpty());
  }

  @Test
  void whenIsbnDefinedButIncorrectThenValidationFails() {
    var book = Book.builder()
        .isbn("1sas234890")
        .title("Title")
        .author("Author")
        .price(9.90)
        .build();
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    Assertions.assertFalse(violations.isEmpty());
    Assertions.assertEquals(violations.iterator().next().getMessage(), "The ISBN format must be valid.");
  }

}
