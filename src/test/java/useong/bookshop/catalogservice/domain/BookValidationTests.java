package useong.bookshop.catalogservice.domain;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class BookValidationTests {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    var book = Book.builder()
        .id(null)
        .isbn("1234567890")
        .titie("Title")
        .author("Author")
        .price(9.90)
        .build();
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbnDefinedButIncorrectThenValidationFails() {
    var book = Book.builder()
        .id(null)
        .isbn("1234567890")
        .titie("Title")
        .author("Author")
        .price(9.90)
        .build();
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    Assertions.assertThat(violations).hasSize(1);
    Assertions.assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The ISBN format must be valid.");
  }

}
