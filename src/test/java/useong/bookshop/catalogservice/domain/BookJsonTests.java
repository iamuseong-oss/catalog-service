package useong.bookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTest
@Profile("test")
public class BookJsonTests {

  private JacksonTester<Book> json;

  public BookJsonTests() {
    ObjectMapper mapper = new ObjectMapper();
    JacksonTester.initFields(this, mapper);
  }

  @Test
  public void testSerialize() throws Exception {
    Book book = Book.builder()
        .isbn("1234567890")
        .title("Title")
        .author("Author")
        .price(9.90)
        .build();

    JsonContent<Book> jsonContent = json.write(book);

    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.isbn")
        .isEqualTo(book.getIsbn());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.title")
        .isEqualTo(book.getTitle());
    assertThat(jsonContent)
        .extractingJsonPathStringValue("@.author")
        .isEqualTo(book.getAuthor());
    assertThat(jsonContent)
        .extractingJsonPathNumberValue("@.price")
        .isEqualTo(book.getPrice());
  }

  @Test
  public void testDeserialize() throws Exception {
    String content = """
            {
        "isbn": "1234567890",
        "title": "Title",
        "author": "Author",
        "price": 9.90
        }
            """;

    assertThat(json.parse(content))
        .usingRecursiveComparison().isEqualTo(Book.builder()
            .isbn("1234567890")
            .title("Title")
            .author("Author")
            .price(9.90)
            .build());
  }
}
