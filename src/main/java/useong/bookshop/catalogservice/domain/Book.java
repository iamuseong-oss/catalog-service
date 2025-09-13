package useong.bookshop.catalogservice.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_seq_gen")
  @SequenceGenerator(name = "books_seq_gen", sequenceName = "books_seq", allocationSize = 1)
  private Long id;

  @NotBlank(message = "The book ISBN must be defined.")
  @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
  private String isbn;

  @NotBlank(message = "The book title must be defined.")
  private String title;

  @NotBlank(message = "The book author must be defined.")
  private String author;

  @NotNull(message = "The book price must be defined.")
  @Positive(message = "The book price must be grater then zero.")
  private Double price;

  private String publisher;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @Version
  @Builder.Default
  private Long version = 0L;

}
