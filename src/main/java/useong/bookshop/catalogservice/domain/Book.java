package useong.bookshop.catalogservice.domain;

import jakarta.persistence.Id;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Getter
public class Book {
        @Id
        private Long id;
        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        private String isbn;
        @NotBlank(message = "The book title must be defined.")
        private String titie;
        @NotBlank(message = "The book author must be defined.")
        private String author;
        @NotNull(message = "The book price must be defined.")
        @Positive(message = "The book price must be grater then zero.")
        private Double price;
        @Version
        @Builder.Default
        private int version = 0;
}
